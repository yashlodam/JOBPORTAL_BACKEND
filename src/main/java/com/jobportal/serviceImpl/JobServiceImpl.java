package com.jobportal.serviceImpl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.JobRequestDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.CompanyRepository;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.RecuriterRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.JobService;

@Service
public class JobServiceImpl implements JobService{

	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RecuriterRepository recruiterRepository;
	
	@Autowired
	private CompanyRepository companyRepository;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public JobResponseDTO createJob(JobRequestDTO dto, String email) throws JobPortalException {

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new JobPortalException("User not found"));

	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() -> new JobPortalException("Recruiter profile not found"));

	    Company company = recruiter.getCompany();

	    if (company == null) {
	        throw new JobPortalException("Recruiter is not associated with any company.");
	    }

	    Job job = mapper.map(dto, Job.class);

	    job.setRecruiter(recruiter);
	    job.setCompany(company);

	    Job savedJob = jobRepository.save(job);

	    return mapper.map(savedJob, JobResponseDTO.class);
	}

	@Override
	public JobResponseDTO updateJob(Long jobId, JobRequestDTO dto, String email) throws JobPortalException {

	    // Find logged in user
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new JobPortalException("User not found"));

	    // Find recruiter
	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() -> new JobPortalException("Recruiter not found"));

	    // Find job
	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() -> new JobPortalException("Job not found"));

	    // Authorization check
	    if (!job.getRecruiter().getId().equals(recruiter.getId())) {
	        throw new JobPortalException("You are not authorized to update this job.");
	    }

	    // Update only non-null fields
	    mapper.map(dto, job);

	    Job updatedJob = jobRepository.save(job);

	    return mapper.map(updatedJob, JobResponseDTO.class);
	}

	@Override
	public void deleteJob(Long jobId, String email) throws JobPortalException {

	    // Find logged-in user
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new JobPortalException("User not found"));

	    // Find recruiter
	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() -> new JobPortalException("Recruiter not found"));

	    // Find job
	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() -> new JobPortalException("Job not found"));

	    // Authorization check
	    if (!job.getRecruiter().getId().equals(recruiter.getId())) {
	        throw new JobPortalException("You are not authorized to delete this job.");
	    }

	    jobRepository.delete(job);
	}

	@Override
	public List<JobResponseDTO> getMyJobs(String email) throws JobPortalException {

	    // Find logged-in user
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new JobPortalException("User not found"));

	    // Find recruiter
	    Recruiter recruiter = recruiterRepository.findByUser(user)
	            .orElseThrow(() -> new JobPortalException("Recruiter not found"));

	    // Get recruiter's jobs
	    List<Job> jobs = jobRepository.findByRecruiter(recruiter);

	    // Convert Entity -> DTO
	    return jobs.stream()
	            .map(job -> mapper.map(job, JobResponseDTO.class))
	            .toList();
	}

	@Override
	public JobResponseDTO getJobById(Long jobId) throws JobPortalException {

	    Job job = jobRepository.findById(jobId)
	            .orElseThrow(() -> new JobPortalException("Job not found with id : " + jobId));

	    return mapper.map(job, JobResponseDTO.class);
	}

	@Override
	public List<JobResponseDTO> getAllJobs() {

	    List<Job> jobs = jobRepository.findAll();

	    return jobs.stream()
	            .map(job -> {
	                JobResponseDTO dto = mapper.map(job, JobResponseDTO.class);

	                // Company Details
	                dto.setCompanyId(job.getCompany().getId());
	                dto.setCompanyName(job.getCompany().getCompanyName());
	                dto.setCompanyLogo(job.getCompany().getLogo());

	                // Recruiter Details
	                dto.setRecruiterId(job.getRecruiter().getId());
	                dto.setRecruiterName(job.getRecruiter().getUser().getName());

	                return dto;
	            })
	            .toList();
	}

	@Override
	public List<JobResponseDTO> searchJobs(String keyword) {

	    List<Job> jobs = jobRepository.searchJobs(keyword);

	    return jobs.stream()
	            .map(job -> {
	                JobResponseDTO dto = mapper.map(job, JobResponseDTO.class);

	                dto.setCompanyId(job.getCompany().getId());
	                dto.setCompanyName(job.getCompany().getCompanyName());
	                dto.setCompanyLogo(job.getCompany().getLogo());

	                dto.setRecruiterId(job.getRecruiter().getId());
	                dto.setRecruiterName(job.getRecruiter().getUser().getName());

	                return dto;
	            })
	            .toList();
	}

	@Override
	public List<JobResponseDTO> filterJobs(
	        String city,
	        String jobType,
	        String workingMode,
	        String experienceLevel,
	        Long minimumSalary,
	        Long maximumSalary) {

	    List<Job> jobs = jobRepository.filterJobs(
	            city,
	            jobType,
	            workingMode,
	            experienceLevel,
	            minimumSalary,
	            maximumSalary);

	    return jobs.stream()
	            .map(job -> {
	                JobResponseDTO dto = mapper.map(job, JobResponseDTO.class);

	                dto.setCompanyId(job.getCompany().getId());
	                dto.setCompanyName(job.getCompany().getCompanyName());
	                dto.setCompanyLogo(job.getCompany().getLogo());

	                dto.setRecruiterId(job.getRecruiter().getId());
	                dto.setRecruiterName(job.getRecruiter().getUser().getName());

	                return dto;
	            })
	            .toList();
	}

	@Override
	public List<JobResponseDTO> getCompanyJobs(Long companyId) throws JobPortalException {

	    Company company = companyRepository.findById(companyId)
	            .orElseThrow(() -> new JobPortalException("Company not found with id : " + companyId));

	    List<Job> jobs = jobRepository.findByCompanyId(companyId);

	    return jobs.stream()
	            .map(job -> {
	                JobResponseDTO dto = mapper.map(job, JobResponseDTO.class);

	                dto.setCompanyId(company.getId());
	                dto.setCompanyName(company.getCompanyName());
	                dto.setCompanyLogo(company.getLogo());

	                dto.setRecruiterId(job.getRecruiter().getId());
	                dto.setRecruiterName(job.getRecruiter().getUser().getName());

	                return dto;
	            })
	            .toList();
	}
	
	@Override
	public List<JobResponseDTO> getJobsByCategory(String category) throws JobPortalException {

	    List<Job> jobs = jobRepository.findByCategoryIgnoreCase(category);

	    if (jobs.isEmpty()) {
	        throw new JobPortalException("No jobs found for category : " + category);
	    }

	    return jobs.stream()
	            .map(job -> {
	                JobResponseDTO dto = mapper.map(job, JobResponseDTO.class);

	                dto.setCompanyId(job.getCompany().getId());
	                dto.setCompanyName(job.getCompany().getCompanyName());
	                dto.setCompanyLogo(job.getCompany().getLogo());

	                dto.setRecruiterId(job.getRecruiter().getId());
	                dto.setRecruiterName(job.getRecruiter().getUser().getName());

	                return dto;
	            })
	            .toList();
	}

	@Override
	public List<JobResponseDTO> latestJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> featuredJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> similarJobs(Long jobId) throws JobPortalException {

	    Job currentJob = jobRepository.findById(jobId)
	            .orElseThrow(() -> new JobPortalException("Job not found"));

	    List<Job> jobs = jobRepository.findSimilarJobs(
	            currentJob.getId(),
	            currentJob.getCategory(),
	            currentJob.getCity(),
	            currentJob.getJobTitle());

	    return jobs.stream()
	            .limit(5)
	            .map(job -> {
	                JobResponseDTO dto = mapper.map(job, JobResponseDTO.class);

	                dto.setCompanyId(job.getCompany().getId());
	                dto.setCompanyName(job.getCompany().getCompanyName());
	                dto.setCompanyLogo(job.getCompany().getLogo());

	                dto.setRecruiterId(job.getRecruiter().getId());
	                dto.setRecruiterName(job.getRecruiter().getUser().getName());

	                return dto;
	            })
	            .toList();
	}

	
}
