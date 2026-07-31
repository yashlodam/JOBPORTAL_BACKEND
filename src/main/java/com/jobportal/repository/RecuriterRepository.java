package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;

public interface RecuriterRepository  extends JpaRepository<Recruiter, Long>{

	Optional<Recruiter> findByUser(User user);
}
