package com.jobportal.dto;

import java.time.LocalDateTime;

import com.jobportal.domain.ApplicationStatus;

public class Applicant {

	private Long applicantId;
	private LocalDateTime timestamp;
	private ApplicationStatus applicationStatus;
	public Long getApplicantId() {
		return applicantId;
	}
	public void setApplicantId(Long applicantId) {
		this.applicantId = applicantId;
	}
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public ApplicationStatus getApplicationStatus() {
		return applicationStatus;
	}
	public void setApplicationStatus(ApplicationStatus applicationStatus) {
		this.applicationStatus = applicationStatus;
	}
	public Applicant(Long applicantId, LocalDateTime timestamp, ApplicationStatus applicationStatus) {
		super();
		this.applicantId = applicantId;
		this.timestamp = timestamp;
		this.applicationStatus = applicationStatus;
	}
	
	public Applicant() {
		
	}
}
