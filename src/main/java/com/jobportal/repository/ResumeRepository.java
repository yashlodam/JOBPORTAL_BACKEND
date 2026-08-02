package com.jobportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Resume;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

}
