package com.jobportal.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jobportal.domain.JobStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==============================
    // Recruiter Relationship
    // ==============================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    @JsonIgnore
    private Recruiter recruiter;

    // ==============================
    // Basic Job Details
    // ==============================

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String workMode;          // Remote, Hybrid, On-site

    @Column(nullable = false)
    private String jobType;           // Full Time, Part Time, Internship

    @Column(nullable = false)
    private String location;

    // ==============================
    // Job Information
    // ==============================

    @Column(length = 5000)
    private String description;

    @Column(length = 3000)
    private String requirements;

    @ElementCollection
    @CollectionTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    private String experienceLevel;

    private Integer minimumExperience;

    private Integer maximumExperience;

    private String education;

    // ==============================
    // Salary
    // ==============================

    private Double minimumSalary;

    private Double maximumSalary;

    private String salaryCurrency = "INR";

    private Boolean salaryNegotiable = false;

    // ==============================
    // Benefits
    // ==============================

    @ElementCollection
    @CollectionTable(
            name = "job_benefits",
            joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "benefit")
    private List<String> benefits = new ArrayList<>();

    // ==============================
    // Hiring Details
    // ==============================

    private Integer openings;

    private LocalDate applicationDeadline;

    private LocalDate joiningDate;

    // ==============================
    // Status
    // ==============================

    @Enumerated(EnumType.STRING)
    private JobStatus status = JobStatus.OPEN;

    // ==============================
    // Statistics
    // ==============================

    private Integer totalViews = 0;

    private Integer totalApplications = 0;

    // ==============================
    // Extra Features
    // ==============================

    private Boolean featuredJob = false;

    private Boolean urgentHiring = false;

    private Boolean easyApply = true;

    private Boolean verifiedCompany = false;

    private Boolean remoteAvailable = false;

    // ==============================
    // Date & Time
    // ==============================

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ==============================
    // Getters & Setters
    // ==============================
}