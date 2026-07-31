package com.jobportal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobportal.entity.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByCompanyNameContainingIgnoreCase(String keyword);

   Optional<Company> findByEmail(String email);
}