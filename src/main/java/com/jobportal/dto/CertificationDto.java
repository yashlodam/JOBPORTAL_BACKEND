package com.jobportal.dto;

import java.time.LocalDate;

public class CertificationDto {

	 private Long id;

	    private String title;

	    private String issuer;

	    private LocalDate issueDate;

	    private String certificateId;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getIssuer() {
			return issuer;
		}

		public void setIssuer(String issuer) {
			this.issuer = issuer;
		}

		public LocalDate getIssueDate() {
			return issueDate;
		}

		public void setIssueDate(LocalDate issueDate) {
			this.issueDate = issueDate;
		}

		public String getCertificateId() {
			return certificateId;
		}

		public void setCertificateId(String certificateId) {
			this.certificateId = certificateId;
		}

		public CertificationDto(Long id, String title, String issuer, LocalDate issueDate, String certificateId) {
			super();
			this.id = id;
			this.title = title;
			this.issuer = issuer;
			this.issueDate = issueDate;
			this.certificateId = certificateId;
		}
	    
		public CertificationDto() {
			
		}
	    
}
