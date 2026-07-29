package com.jobportal.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.jobportal.domain.JobStatus;

public class JobDTO {


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
	private JobStatus jobStatus;
	public Long getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getSkillsRequired() {
		return skillsRequired;
	}

	public void setSkillsRequired(List<String> skillsRequired) {
		this.skillsRequired = skillsRequired;
	}

	

	public JobDTO(Long id, String jobTitle, String company, List<Applicant> applicant, String about, String experience,
			String jobType, String location, Long packageOffered, LocalDateTime postTime, String description,
			List<String> skillsRequired, JobStatus jobStatus) {
		super();
		this.id = id;
		this.jobTitle = jobTitle;
		this.company = company;
		this.applicant = applicant;
		this.about = about;
		this.experience = experience;
		this.jobType = jobType;
		this.location = location;
		this.packageOffered = packageOffered;
		this.postTime = postTime;
		this.description = description;
		this.skillsRequired = skillsRequired;
		this.jobStatus = jobStatus;
	}

	public void setId(Long id) {
		this.id = id;
	}
	public String getJobTitle() {
		return jobTitle;
	}
	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public List<Applicant> getApplicant() {
		return applicant;
	}
	public void setApplicant(List<Applicant> applicant) {
		this.applicant = applicant;
	}
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	public String getExperience() {
		return experience;
	}
	public void setExperience(String experience) {
		this.experience = experience;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Long getPackageOffered() {
		return packageOffered;
	}
	public void setPackageOffered(Long packageOffered) {
		this.packageOffered = packageOffered;
	}
	public LocalDateTime getPostTime() {
		return postTime;
	}
	public void setPostTime(LocalDateTime postTime) {
		this.postTime = postTime;
	}
	public JobStatus getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}
	
}
