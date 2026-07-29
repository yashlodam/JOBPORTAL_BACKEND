package com.jobportal.service;

import java.util.List;

import com.jobportal.dto.JobDTO;

public interface JobService {

	JobDTO postJob(JobDTO jobDTO);

	List<JobDTO> getAllJobs();

	
}
