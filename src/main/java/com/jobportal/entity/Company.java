package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    private String website;

    private String logo;

    private String industry;

    private String companySize;

    private String headquarters;

    private String foundedYear;

    private String email;

    private String phone;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String mission;

    @Column(length = 2000)
    private String benefits;

    @OneToMany(mappedBy = "company")
    private List<Recruiter> recruiters;

    @OneToMany(mappedBy = "company")
    private List<Job> jobs;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    @PrePersist
    public void onCreate() {
        createdOn = LocalDateTime.now();
        updatedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedOn = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCompanySize() {
		return companySize;
	}

	public void setCompanySize(String companySize) {
		this.companySize = companySize;
	}

	public String getHeadquarters() {
		return headquarters;
	}

	public void setHeadquarters(String headquarters) {
		this.headquarters = headquarters;
	}

	public String getFoundedYear() {
		return foundedYear;
	}

	public void setFoundedYear(String foundedYear) {
		this.foundedYear = foundedYear;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public List<Recruiter> getRecruiters() {
		return recruiters;
	}

	public void setRecruiters(List<Recruiter> recruiters) {
		this.recruiters = recruiters;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(LocalDateTime updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Company(Long id, String companyName, String website, String logo, String industry, String companySize,
			String headquarters, String foundedYear, String email, String phone, String description, String mission,
			String benefits, List<Recruiter> recruiters, List<Job> jobs, LocalDateTime createdOn,
			LocalDateTime updatedOn) {
		super();
		this.id = id;
		this.companyName = companyName;
		this.website = website;
		this.logo = logo;
		this.industry = industry;
		this.companySize = companySize;
		this.headquarters = headquarters;
		this.foundedYear = foundedYear;
		this.email = email;
		this.phone = phone;
		this.description = description;
		this.mission = mission;
		this.benefits = benefits;
		this.recruiters = recruiters;
		this.jobs = jobs;
		this.createdOn = createdOn;
		this.updatedOn = updatedOn;
	}
    
    public Company() {
    	
    }
    
}
