package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.jobportal.domain.JobStatus;
import com.jobportal.dto.Applicant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	
	private String jobTitle;
	private String company;
	private List<Applicant> applicant;
	private String about;
	private String experience;
	private String jobType;
	private String location;
	private Long packageOffered;
	private LocalDateTime postTime;
	private String description;
	private List<String> skillsRequired;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private JobStatus jobStatus = JobStatus.DRAFT;
}
