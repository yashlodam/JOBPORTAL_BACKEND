package com.jobportal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.JobRequestDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/jobs")
public class JobController {

	@Autowired
	private JobService jobService;
	
	@PostMapping
	public ResponseEntity<JobResponseDTO> createJob(
	        @Valid @RequestBody JobRequestDTO dto,
	        Authentication authentication)
	        throws JobPortalException {

	    String email = authentication.getName();

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(jobService.createJob(dto, email));
	}
	
	
	@PutMapping("/{jobId}")
	public ResponseEntity<JobResponseDTO> updateJob(
	        @PathVariable Long jobId,
	        @Valid @RequestBody JobRequestDTO dto,
	        Authentication authentication)
	        throws JobPortalException {

	    String email = authentication.getName();

	    return ResponseEntity.ok(
	            jobService.updateJob(jobId, dto, email));
	}
	
	
	@DeleteMapping("/{jobId}")
	public ResponseEntity<String> deleteJob(
	        @PathVariable Long jobId,
	        Authentication authentication)
	        throws JobPortalException {

	    String email = authentication.getName();

	    jobService.deleteJob(jobId, email);

	    return ResponseEntity.ok("Job deleted successfully.");
	}
	
	
	@GetMapping("/{jobId}")
	public ResponseEntity<JobResponseDTO> getJobById(
	        @PathVariable Long jobId)
	        throws JobPortalException {

	    return ResponseEntity.ok(
	            jobService.getJobById(jobId));
	}
	
	
	@GetMapping
	public ResponseEntity<List<JobResponseDTO>> getAllJobs() {

	    return ResponseEntity.ok(
	            jobService.getAllJobs());
	}
	
	
	@GetMapping("/me")
	public ResponseEntity<List<JobResponseDTO>> getMyJobs(
	        Authentication authentication)
	        throws JobPortalException {

	    String email = authentication.getName();

	    return ResponseEntity.ok(
	            jobService.getMyJobs(email));
	}
	
	
	@GetMapping("/search")
	public ResponseEntity<List<JobResponseDTO>> searchJobs(
	        @RequestParam String keyword) {

	    return ResponseEntity.ok(
	            jobService.searchJobs(keyword));
	}
	
	
	@GetMapping("/filter")
	public ResponseEntity<List<JobResponseDTO>> filterJobs(

	        @RequestParam(required = false) String location,

	        @RequestParam(required = false) String jobType,

	        @RequestParam(required = false) String workingMode,

	        @RequestParam(required = false) String experienceLevel,

	        @RequestParam(required = false) Long minimumSalary,

	        @RequestParam(required = false) Long maximumSalary) {

	    return ResponseEntity.ok(
	            jobService.filterJobs(
	                    location,
	                    jobType,
	                    workingMode,
	                    experienceLevel,
	                    minimumSalary,
	                    maximumSalary));
	}
	
	@GetMapping("/company/{companyId}")
	public ResponseEntity<List<JobResponseDTO>> getCompanyJobs(
	        @PathVariable Long companyId)
	        throws JobPortalException {

	    return ResponseEntity.ok(
	            jobService.getCompanyJobs(companyId));
	}
	
	
	
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<JobResponseDTO>> getJobsByCategory(
	        @PathVariable Long categoryId)
	        throws JobPortalException {

	    return ResponseEntity.ok(
	            jobService.getJobsByCategory(categoryId));
	}
	
	
	
	@GetMapping("/latest")
	public ResponseEntity<List<JobResponseDTO>> latestJobs() {

	    return ResponseEntity.ok(
	            jobService.latestJobs());
	}
	
	
	@GetMapping("/featured")
	public ResponseEntity<List<JobResponseDTO>> featuredJobs() {

	    return ResponseEntity.ok(
	            jobService.featuredJobs());
	}
	
	
	
	@GetMapping("/{jobId}/similar")
	public ResponseEntity<List<JobResponseDTO>> similarJobs(
	        @PathVariable Long jobId)
	        throws JobPortalException {

	    return ResponseEntity.ok(
	            jobService.similarJobs(jobId));
	}
}
