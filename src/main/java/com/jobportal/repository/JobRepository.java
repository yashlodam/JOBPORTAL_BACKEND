package com.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;

public interface JobRepository extends JpaRepository<Job, Long>{

	List<Job> findByCompany(Company company);

	List<Job> findByRecruiter(Recruiter recruiter);
}
