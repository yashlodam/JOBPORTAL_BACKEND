package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobportal.entity.JobApplication;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    boolean existsByApplicantIdAndJobId(Long applicantId, Long jobId);

    @EntityGraph(attributePaths = {"job", "job.company", "applicant", "resume"})
    Page<JobApplication> findByApplicantId(Long applicantId, Pageable pageable);

    @EntityGraph(attributePaths = {"job", "job.company", "applicant", "resume"})
    Page<JobApplication> findByJobId(Long jobId, Pageable pageable);

    @EntityGraph(attributePaths = {"job", "job.company", "applicant", "resume"})
    Optional<JobApplication> findByApplicantIdAndJobId(Long applicantId, Long jobId);

    @Query("SELECT COUNT(ja) FROM JobApplication ja WHERE ja.job.id = :jobId")
    long countByJobId(@Param("jobId") Long jobId);
}
