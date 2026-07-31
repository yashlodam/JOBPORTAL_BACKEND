package com.jobportal.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.JobDTO;
import com.jobportal.dto.JobRequestDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> getAllJobs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> searchJobs(String keyword) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> filterJobs(String city, String jobType, String workingMode, String experienceLevel,
			Long minimumSalary, Long maximumSalary) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> getCompanyJobs(Long companyId) throws JobPortalException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobResponseDTO> getJobsByCategory(Long categoryId) throws JobPortalException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	
}
