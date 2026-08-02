package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.SavedJob;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

    boolean existsByUserIdAndJobId(Long userId, Long jobId);

    Optional<SavedJob> findByUserIdAndJobId(Long userId, Long jobId);

    @EntityGraph(attributePaths = {"job", "job.company", "job.recruiter", "job.recruiter.user"})
    Page<SavedJob> findByUserId(Long userId, Pageable pageable);
}
