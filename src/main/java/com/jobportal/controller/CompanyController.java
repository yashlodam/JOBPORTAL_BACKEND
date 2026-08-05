package com.jobportal.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.dto.CompanyRequestDTO;
import com.jobportal.dto.CompanyResponseDTO;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.JobSummaryResponse;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.CompanyService;

import jakarta.validation.Valid;

/**
 * Company controller — all endpoints under /api/companies.
 * Public read endpoints are permitted in SecurityConfig.
 * Write endpoints require authentication.
 */
@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // ── Write Endpoints (Authenticated) ──────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> createCompany(
            @Valid @RequestBody CompanyRequestDTO dto,
            Authentication authentication) throws JobPortalException {
        CompanyResponseDTO company = companyService.createCompany(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Company created successfully", company));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> getMyCompany(
            Authentication authentication) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                companyService.getMyCompany(authentication.getName())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> updateCompany(
            @Valid @RequestBody CompanyRequestDTO dto,
            Authentication authentication) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success("Company updated successfully",
                companyService.updateCompany(dto, authentication.getName())));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteCompany(
            Authentication authentication) throws JobPortalException {
        companyService.deleteCompany(authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("Company deleted successfully"));
    }

    @PostMapping("/me/logo")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> uploadLogo(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws Exception {
        return ResponseEntity.ok(ApiResponse.success("Logo uploaded successfully",
                companyService.uploadLogo(file, authentication.getName())));
    }

    @PostMapping("/me/cover")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> uploadCoverImage(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws Exception {
        return ResponseEntity.ok(ApiResponse.success("Cover image uploaded successfully",
                companyService.uploadCoverImage(file, authentication.getName())));
    }

    @GetMapping("/me/jobs")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getMyCompanyJobs(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable)
            throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                companyService.getMyCompanyJobs(authentication.getName(), pageable)));
    }

    // ── Public Read Endpoints ─────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CompanyResponseDTO>>> getAllCompanies(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(companyService.getAllCompanies(pageable)));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponseDTO>> getCompanyById(
            @PathVariable Long companyId) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(companyService.getCompanyById(companyId)));
    }
    
    @GetMapping("/{companyId}/jobs")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getCompanyJobs(
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable)
            throws JobPortalException {

        return ResponseEntity.ok(
                ApiResponse.success(
                        companyService.getCompanyJobs(companyId, pageable)
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CompanyResponseDTO>>> searchCompanies(
            @RequestParam String keyword,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                companyService.searchCompanies(keyword, pageable)));
    }
}
