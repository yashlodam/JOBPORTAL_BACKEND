package com.jobportal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.CompanyResponseDTO;
import com.jobportal.dto.JobResponseDTO;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.CompanyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/company")
public class ComapnyController {

    private final CompanyService companyService;

    public ComapnyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyResponseDTO> createCompany(
            @RequestBody CompanyRequestDTO companyRequestDTO,
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        CompanyResponseDTO company =
                companyService.createCompany(companyRequestDTO, email);

        return ResponseEntity.status(HttpStatus.CREATED).body(company);
    }
    
    
    @GetMapping("/me")
    public ResponseEntity<CompanyResponseDTO> getMyCompany(
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        return ResponseEntity.ok(
                companyService.getMyCompany(email));
    }
    
    
    @PutMapping("/me")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
            @Valid @RequestBody CompanyRequestDTO companyRequestDTO,
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        return ResponseEntity.ok(
                companyService.updateCompany(companyRequestDTO, email));
    }
    
    
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteCompany(
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        companyService.deleteCompany(email);

        return ResponseEntity.ok("Company deleted successfully.");
    }
    
    
    
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(
            @PathVariable Long companyId)
            throws JobPortalException {

        return ResponseEntity.ok(
                companyService.getCompanyById(companyId));
    }
    
    
    
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> getAllCompanies() {

        return ResponseEntity.ok(
                companyService.getAllCompanies());
    }
    
    
    
    @GetMapping("/search")
    public ResponseEntity<List<CompanyResponseDTO>> searchCompanies(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                companyService.searchCompanies(keyword));
    }
    
    
    @PostMapping("/logo")
    public ResponseEntity<CompanyResponseDTO> uploadLogo(
            @RequestParam MultipartFile file,
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        return ResponseEntity.ok(
                companyService.uploadLogo(file, email));
    }
    
    
    @PostMapping("/cover")
    public ResponseEntity<CompanyResponseDTO> uploadCoverImage(
            @RequestParam MultipartFile file,
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        return ResponseEntity.ok(
                companyService.uploadCoverImage(file, email));
    }
    
    @GetMapping("/me/jobs")
    public ResponseEntity<List<JobResponseDTO>> getMyCompanyJobs(
            Authentication authentication)
            throws JobPortalException {

        String email = authentication.getName();

        return ResponseEntity.ok(
                companyService.getMyCompanyJobs(email));
    }

}