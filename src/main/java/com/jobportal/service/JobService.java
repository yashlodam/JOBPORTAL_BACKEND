package com.jobportal.service;

import java.util.List;

import com.jobportal.dto.JobRequestDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.exception.JobPortalException;

public interface JobService {

    // Recruiter APIs
    JobResponseDTO createJob(JobRequestDTO dto, String email)
            throws JobPortalException;

    JobResponseDTO updateJob(Long jobId, JobRequestDTO dto, String email)
            throws JobPortalException;

    void deleteJob(Long jobId, String email)
            throws JobPortalException;

    List<JobResponseDTO> getMyJobs(String email)
            throws JobPortalException;

    // Public APIs
    JobResponseDTO getJobById(Long jobId)
            throws JobPortalException;

    List<JobResponseDTO> getAllJobs();

    List<JobResponseDTO> searchJobs(String keyword);

    List<JobResponseDTO> filterJobs(
            String city,
            String jobType,
            String workingMode,
            String experienceLevel,
            Long minimumSalary,
            Long maximumSalary);

    List<JobResponseDTO> getCompanyJobs(Long companyId)
            throws JobPortalException;

    List<JobResponseDTO> getJobsByCategory(String category)
            throws JobPortalException;

    List<JobResponseDTO> latestJobs();

    List<JobResponseDTO> featuredJobs();

    List<JobResponseDTO> similarJobs(Long jobId)
            throws JobPortalException;
}