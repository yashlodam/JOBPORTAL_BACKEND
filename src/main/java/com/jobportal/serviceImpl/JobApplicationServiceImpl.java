package com.jobportal.serviceImpl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.domain.AccountType;
import com.jobportal.dto.request.JobApplicationRequest;
import com.jobportal.dto.request.UpdateApplicationStatusRequest;
import com.jobportal.dto.response.JobApplicationResponse;
import com.jobportal.entity.Job;
import com.jobportal.entity.JobApplication;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.Resume;
import com.jobportal.entity.User;
import com.jobportal.event.ApplicationStatusChangedEvent;
import com.jobportal.event.ApplicationSubmittedEvent;
import com.jobportal.event.ApplicationWithdrawnEvent;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.JobApplicationRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecruiterRepository;
import com.jobportal.repository.ResumeRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobApplicationService;

/**
 * Implements job-application business logic.
 *
 * <h3>Notification decoupling</h3>
 * <p>This service no longer depends on {@link com.jobportal.service.NotificationService}
 * directly. Instead, it publishes domain events via {@link ApplicationEventPublisher}.
 * The {@link com.jobportal.listener.NotificationEventListener} handles these events
 * and creates notifications independently, which:
 * <ul>
 *   <li>Eliminates tight coupling between the Application module and the Notification module.</li>
 *   <li>Ensures notifications are only sent AFTER the business transaction commits
 *       (via {@code @TransactionalEventListener(AFTER_COMMIT)}).</li>
 *   <li>Makes it easy to add other listeners in the future (e.g. email, WebSocket push)
 *       without modifying this class.</li>
 * </ul>
 * </p>
 */
@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository            jobRepository;
    private final UserRepository           userRepository;
    private final RecruiterRepository      recruiterRepository;
    private final ResumeRepository         resumeRepository;
    private final com.jobportal.repository.ProfileRepository profileRepository;
    private final com.jobportal.service.ResumeService resumeService;
    private final ApplicationEventPublisher eventPublisher;

    public JobApplicationServiceImpl(
            JobApplicationRepository applicationRepository,
            JobRepository jobRepository,
            UserRepository userRepository,
            RecruiterRepository recruiterRepository,
            ResumeRepository resumeRepository,
            com.jobportal.repository.ProfileRepository profileRepository,
            com.jobportal.service.ResumeService resumeService,
            ApplicationEventPublisher eventPublisher) {
        this.applicationRepository = applicationRepository;
        this.jobRepository         = jobRepository;
        this.userRepository        = userRepository;
        this.recruiterRepository   = recruiterRepository;
        this.resumeRepository      = resumeRepository;
        this.profileRepository     = profileRepository;
        this.resumeService        = resumeService;
        this.eventPublisher        = eventPublisher;
    }

    // ── Apply ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public JobApplicationResponse applyToJob(Long jobId,
                                              JobApplicationRequest request,
                                              String email)
            throws JobPortalException {
        User applicant = findUserByEmail(email);

        if (applicant.getAccountType() != AccountType.APPLICANT) {
            throw JobPortalException.forbidden("Only applicants can apply for jobs.");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobPortalException.notFound(
                        "Job not found with id: " + jobId));

        if (applicationRepository.existsByApplicantIdAndJobId(applicant.getId(), jobId)) {
            throw JobPortalException.conflict("You have already applied for this job.");
        }

        Resume selectedResume = null;

        if (request.getResumeId() != null) {
            selectedResume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> JobPortalException.notFound("Resume not found with id: " + request.getResumeId()));
            if (!selectedResume.getProfile().getUser().getId().equals(applicant.getId())) {
                throw JobPortalException.forbidden("This resume does not belong to you.");
            }
        } else {
            com.jobportal.entity.Profile profile = profileRepository.findByUserEmail(email)
                    .orElseThrow(() -> JobPortalException.notFound("Profile not found."));

            selectedResume = resumeRepository.findByProfileIdAndIsDefaultTrue(profile.getId())
                    .orElseGet(() -> {
                        java.util.List<Resume> userResumes = resumeRepository.findByProfileIdOrderByIsDefaultDescCreatedAtDesc(profile.getId());
                        return userResumes.isEmpty() ? null : userResumes.get(0);
                    });

            if (selectedResume == null) {
                throw JobPortalException.badRequest("Please upload a resume before applying.");
            }
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setApplicant(applicant);
        application.setCoverLetter(request.getCoverLetter());
        application.setResume(selectedResume);

        // Increment applicant count atomically before saving the application
        job.setTotalApplicants(job.getTotalApplicants() + 1);
        jobRepository.save(job);

        JobApplication saved = applicationRepository.save(application);

        // ── Publish event: listener handles notification creation ──────────
        User recruiterUser = job.getRecruiter().getUser();
        eventPublisher.publishEvent(new ApplicationSubmittedEvent(
                this, applicant, recruiterUser, job, saved.getId()));

        return toResponse(saved);
    }

    // ── Withdraw ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void withdrawApplication(Long applicationId, String email)
            throws JobPortalException {
        User applicant = findUserByEmail(email);
        JobApplication application = findApplicationById(applicationId);

        if (!application.getApplicant().getId().equals(applicant.getId())) {
            throw JobPortalException.forbidden(
                    "You are not authorized to withdraw this application.");
        }

        Job job = application.getJob();

        // Capture data before deletion — listener runs after commit with these scalars
        String applicantName   = applicant.getName();
        Long   recruiterUserId = job.getRecruiter().getUser().getId();
        String jobTitle        = job.getJobTitle();
        Long   jobId           = job.getId();

        if (job.getTotalApplicants() > 0) {
            job.setTotalApplicants(job.getTotalApplicants() - 1);
            jobRepository.save(job);
        }

        applicationRepository.delete(application);

        // ── Publish event: listener handles notification creation ──────────
        // Pass only scalars — entity is deleted; listener runs after commit
        eventPublisher.publishEvent(new ApplicationWithdrawnEvent(
                this, applicantName, recruiterUserId, jobTitle, jobId, applicationId));
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getMyApplications(String email, Pageable pageable)
            throws JobPortalException {
        User applicant = findUserByEmail(email);
        return applicationRepository.findByApplicantId(applicant.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobApplicationResponse> getJobApplications(Long jobId,
                                                            String email,
                                                            Pageable pageable)
            throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = recruiterRepository.findByUser(user)
                .orElseThrow(() -> JobPortalException.forbidden(
                        "Only recruiters can view job applications."));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> JobPortalException.notFound("Job not found."));

        if (!job.getRecruiter().getId().equals(recruiter.getId())) {
            throw JobPortalException.forbidden(
                    "You can only view applications for your own jobs.");
        }

        return applicationRepository.findByJobId(jobId, pageable).map(this::toResponse);
    }

    // ── Status Update ─────────────────────────────────────────────────────────

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long applicationId,
                                                           UpdateApplicationStatusRequest request,
                                                           String email)
            throws JobPortalException {
        User user = findUserByEmail(email);
        Recruiter recruiter = recruiterRepository.findByUser(user)
                .orElseThrow(() -> JobPortalException.forbidden(
                        "Only recruiters can update application status."));

        JobApplication application = findApplicationById(applicationId);

        if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
            throw JobPortalException.forbidden(
                    "You are not authorized to update this application.");
        }

        application.setStatus(request.getStatus());
        JobApplication updated = applicationRepository.save(application);

        // ── Publish event: listener maps status → NotificationType ────────
        eventPublisher.publishEvent(
                new ApplicationStatusChangedEvent(this, updated, request.getStatus()));

        return toResponse(updated);
    }

    // ── Private Helpers ─────────────────────────────────────────────────────

    private User findUserByEmail(String email) throws JobPortalException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found."));
    }

    private JobApplication findApplicationById(Long id) throws JobPortalException {
        return applicationRepository.findById(id)
                .orElseThrow(() -> JobPortalException.notFound(
                        "Application not found with id: " + id));
    }

    private JobApplicationResponse toResponse(JobApplication app) {
        JobApplicationResponse dto = new JobApplicationResponse();
        dto.setId(app.getId());
        dto.setStatus(app.getStatus());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setAppliedAt(app.getCreatedAt());
        dto.setUpdatedAt(app.getUpdatedAt());

        if (app.getJob() != null) {
            dto.setJobId(app.getJob().getId());
            dto.setJobTitle(app.getJob().getJobTitle());
            if (app.getJob().getCompany() != null) {
                dto.setCompanyName(app.getJob().getCompany().getCompanyName());
                dto.setCompanyLogo(app.getJob().getCompany().getLogo());
            }
        }
        if (app.getApplicant() != null) {
            dto.setApplicantId(app.getApplicant().getId());
            dto.setApplicantName(app.getApplicant().getName());
            dto.setApplicantEmail(app.getApplicant().getEmail());
        }
        if (app.getResume() != null) {
            dto.setResumeUrl(app.getResume().getResumeUrl());
            dto.setResume(resumeService.toResponse(app.getResume()));
        }
        return dto;
    }
}
