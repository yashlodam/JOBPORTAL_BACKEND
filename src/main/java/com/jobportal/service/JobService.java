package com.jobportal.service;

import java.util.List;


import com.jobportal.dto.JobDTO;
import com.jobportal.exception.JobPortalException;

public interface JobService {

	JobDTO postJob(JobDTO jobDTO);

	List<JobDTO> getAllJobs();

	JobDTO getJob(Long id) throws JobPortalException;

	

	
}
