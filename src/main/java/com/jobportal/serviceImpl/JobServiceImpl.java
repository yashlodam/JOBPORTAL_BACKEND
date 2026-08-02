package com.jobportal.serviceImpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.domain.JobStatus;
import com.jobportal.dto.request.JobFilterRequest;
import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.CategoryResponse;
import com.jobportal.dto.response.JobDetailResponse;
import com.jobportal.dto.response.JobSummaryResponse;
import com.jobportal.dto.response.WorkModeResponse;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.mapper.JobMapper;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.repository.specification.JobSpecification;
import com.jobportal.service.JobService;

/**
 * Job service implementation.
 *
 * <h3>Query Strategy</h3>
 *
 * <h4>Create</h4>
 * <p>New Job entity — {@code save()} persists it. Skills collections are empty
 * {@code ArrayList}s (initialized in entity constructor), so
 * {@code applyRequest} can call {@code .clear() + .addAll()} safely
 * with no lazy-load issue.</p>
 *
 * <h4>Update</h4>
 * <p>Load via {@code findByIdWithDetails} (EntityGraph: company + recruiter +
 * recruiter.user). This also initializes {@code skillsRequired} and
 * {@code preferredSkills} as proper Hibernate collections within the session,
 * so {@code applyRequest}'s {@code .clear() + .addAll()} does not trigger a
 * {@code LazyInitializationException}.</p>
 *
 * <h4>Read (lists)</h4>
 * <p>All paginated list queries use named JPQL methods in {@link JobRepository}
 * that have an explicit {@code countQuery}. EntityGraph is applied to the data
 * query — company and recruiter.user are loaded in one JOIN. skillsRequired is
 * loaded by {@code @BatchSize(25)} in one additional batch query.</p>
 *
 * <h4>Read (filter/search via Specifications)</h4>
 * <p>{@code @EntityGraph} does NOT apply to {@code JpaSpecificationExecutor.findAll}.
 * Company and recruiter are therefore loaded by Hibernate's {@code @BatchSize}
 * (set on the entity associations via the global property) in two additional
 * batch queries. Total for a page of N: 1 (data) + 1 (count) + 2 (batch) = 4 SQL,
 * constant regardless of N.</p>
 *
 * <h4>View count increment</h4>
 * <p>Uses a dedicated {@code @Modifying} UPDATE query that touches only one
 * column — no entity load + save cycle needed.</p>
 */
@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final CompanyRepository companyRepository;
    private final JobMapper jobMapper;

    public JobServiceImpl(
            JobRepository jobRepository,
            UserRepository userRepository,
            RecruiterRepository recruiterRepository,
            CompanyRepository companyRepository,
            JobMapper jobMapper) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.recruiterRepository = recruiterRepository;
        this.companyRepository = companyRepository;
        this.jobMapper = jobMapper;
    }

    // ── Create ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public JobDetailResponse createJob(JobRequest dto, String email) throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = findRecruiterByUser(user);
        Company company = recruiter.getCompany();

        if (company == null) {
            throw JobPortalException.badRequest(
                    "You must create a company profile before posting jobs.");
        }

        Job job = new Job();
        jobMapper.applyRequest(dto, job);
        job.setRecruiter(recruiter);
        job.setCompany(company);
        job.setStatus(JobStatus.OPEN);

        Job saved = jobRepository.save(job);
        // Re-fetch with full details so the mapper can access company + recruiter.user
        return jobMapper.toDetail(findJobByIdWithDetails(saved.getId()));
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public JobDetailResponse updateJob(Long jobId, JobRequest dto, String email)
            throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = findRecruiterByUser(user);

        // Use findByIdWithDetails: loads company + recruiter + recruiter.user
        // AND initializes skillsRequired/preferredSkills within this session
        // so applyRequest's .clear() + .addAll() are safe.
        Job job = findJobByIdWithDetails(jobId);

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw JobPortalException.forbidden("You are not authorized to update this job.");
        }

        jobMapper.applyRequest(dto, job);
        jobRepository.save(job);
        return jobMapper.toDetail(findJobByIdWithDetails(jobId));
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteJob(Long jobId, String email) throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = findRecruiterByUser(user);

        // Lightweight check — no EntityGraph needed
        if (!jobRepository.existsByIdAndRecruiterId(jobId, recruiter.getId())) {
            // Either job does not exist, or belongs to a different recruiter
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> JobPortalException.notFound("Job not found with id: " + jobId));
            throw JobPortalException.forbidden("You are not authorized to delete this job.");
        }

        jobRepository.deleteById(jobId);
    }

    // ── Single Job Read ───────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public JobDetailResponse getJobById(Long jobId) throws JobPortalException {
        return jobMapper.toDetail(findJobByIdWithDetails(jobId));
    }

    // ── View Count ────────────────────────────────────────────────────────────

    /**
     * Increments view count atomically via a single UPDATE, then returns
     * the full job detail. Two queries total, no entity load + save cycle.
     */
    @Override
    @Transactional
    public JobDetailResponse incrementViewAndGet(Long jobId) throws JobPortalException {
        // Validate existence first to return a proper 404 if not found
        if (!jobRepository.existsById(jobId)) {
            throw JobPortalException.notFound("Job not found with id: " + jobId);
        }
        jobRepository.incrementViewCount(jobId);
        return jobMapper.toDetail(findJobByIdWithDetails(jobId));
    }

    // ── Public Lists ──────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getAllJobs(Pageable pageable) {
        return jobRepository.findAllByStatus(JobStatus.OPEN, pageable)
                .map(jobMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> latestJobs() {
        return jobRepository.findTop10OpenJobs(JobStatus.OPEN)
                .stream()
                .map(jobMapper::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> featuredJobs(Pageable pageable) {
        return jobRepository.findFeaturedByStatus(JobStatus.OPEN, pageable)
                .map(jobMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobSummaryResponse> similarJobs(Long jobId) throws JobPortalException {
        Job currentJob = findJobByIdWithDetails(jobId);
        Pageable limit = PageRequest.of(0, 5);
        return jobRepository.findSimilarJobs(
                currentJob.getId(),
                JobStatus.OPEN,
                currentJob.getCategory(),
                currentJob.getCity(),
                currentJob.getJobTitle(),
                limit
        ).stream().map(jobMapper::toSummary).toList();
    }

    // ── Recruiter / Company Scoped ────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getMyJobs(String email, Pageable pageable)
            throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = findRecruiterByUser(user);
        return jobRepository.findByRecruiterWithDetails(recruiter, pageable)
                .map(jobMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getCompanyJobs(Long companyId, Pageable pageable)
            throws JobPortalException {
        if (!companyRepository.existsById(companyId)) {
            throw JobPortalException.notFound("Company not found with id: " + companyId);
        }
        return jobRepository.findByCompanyIdAndStatus(companyId, JobStatus.OPEN, pageable)
                .map(jobMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> getJobsByCategory(String category, Pageable pageable) {
        return jobRepository.findByCategoryAndStatus(category, JobStatus.OPEN, pageable)
                .map(jobMapper::toSummary);
    }

    // ── Search & Filter (Specification) ──────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> searchJobs(String keyword, Pageable pageable) {
        JobFilterRequest filter = new JobFilterRequest();
        filter.setKeyword(keyword);
        Specification<Job> spec = JobSpecification.buildFrom(filter);
        return jobRepository.findAll(spec, pageable).map(jobMapper::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobSummaryResponse> filterJobs(
            JobFilterRequest request,
            Pageable pageable) {

        Specification<Job> specification =
                JobSpecification.buildFrom(request);

        return jobRepository.findAll(specification, pageable)
                .map(jobMapper::toSummary);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private User findUserByEmail(String email) throws JobPortalException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found"));
    }

    private Recruiter findRecruiterByUser(User user) throws JobPortalException {
        return recruiterRepository.findByUser(user)
                .orElseThrow(() -> JobPortalException.forbidden(
                        "Recruiter profile not found. Only recruiters can manage jobs."));
    }

    private Job findJobByIdWithDetails(Long jobId) throws JobPortalException {
        return jobRepository.findByIdWithDetails(jobId)
                .orElseThrow(() -> JobPortalException.notFound("Job not found with id: " + jobId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return jobRepository.getCategoryCount();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkModeResponse> getWorkModes() {
        return jobRepository.getWorkModeCount();
    }
}
