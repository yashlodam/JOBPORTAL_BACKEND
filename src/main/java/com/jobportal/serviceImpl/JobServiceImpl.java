package com.jobportal.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.repository.JobRepository;
import com.jobportal.service.JobService;

@Service
public class JobServiceImpl implements JobService{

	@Autowired
	private JobRepository jobRepository;
}
