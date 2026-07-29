package com.jobportal.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.jobportal.domain.RecruiterStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "recruiters")
public class Recruiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==============================
    // Company Information
    // ==============================

    @Column(nullable = false)
    private String companyName;

    private String companyLogo;

    @Column(unique = true, nullable = false)
    private String companyEmail;

    private String companyPhone;

    private String companyWebsite;

    @Column(length = 3000)
    private String companyDescription;

    private String industry;

    private String companySize;

    private String foundedYear;

    // ==============================
    // Address
    // ==============================

    private String country;

    private String state;

    private String city;

    private String address;

    private String pincode;

    // ==============================
    // HR Information
    // ==============================

    private String hrName;

    private String hrDesignation;

    private String hrEmail;

    private String hrPhone;

    private String linkedinProfile;

    // ==============================
    // Verification
    // ==============================

    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    private RecruiterStatus status = RecruiterStatus.ACTIVE;

    // ==============================
    // Analytics
    // ==============================

    private Integer totalJobs = 0;

    private Integer totalEmployees = 0;

    // ==============================
    // Relationship
    // ==============================

    @OneToMany(
            mappedBy = "recruiter",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Job> jobs = new ArrayList<>();

    // ==============================
    // Audit
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