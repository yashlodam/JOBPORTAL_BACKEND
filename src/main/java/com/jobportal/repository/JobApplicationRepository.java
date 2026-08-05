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

    /**
     * Returns all applicant user IDs for a given job.
     * Used by {@link com.jobportal.serviceImpl.JobServiceImpl#deleteJob}
     * to capture IDs before the job entity is deleted, so the
     * {@link com.jobportal.event.JobDeletedEvent} listener can notify them.
     */
    @Query("SELECT ja.applicant.id FROM JobApplication ja WHERE ja.job.id = :jobId")
    java.util.List<Long> findApplicantUserIdsByJobId(@Param("jobId") Long jobId);
}
