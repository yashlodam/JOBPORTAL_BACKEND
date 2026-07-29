package com.jobportal.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jobportal.dto.JobDTO;
import com.jobportal.entity.Job;
import com.jobportal.repository.JobRepository;
import com.jobportal.service.JobService;

@Service
public class JobServiceImpl implements JobService{

	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public JobDTO postJob(JobDTO jobDTO) {
		
		jobDTO.setPostTime(LocalDateTime.now());
		
		Job job = mapper.map(jobDTO,Job.class);
		
	
		Job j = jobRepository.save(job);
		
		
		return mapper.map(j, JobDTO.class);
	}

	@Override
	public List<JobDTO> getAllJobs() {
		
		List<Job> jobs = jobRepository.findAll();
		
		return null;
	}
}
