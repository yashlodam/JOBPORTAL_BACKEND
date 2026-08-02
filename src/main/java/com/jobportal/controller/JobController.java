package com.jobportal.controller;

import java.util.List;

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

import com.jobportal.domain.ExperienceLevel;
import com.jobportal.domain.JobType;
import com.jobportal.domain.WorkingMode;
import com.jobportal.dto.request.JobFilterRequest;
import com.jobportal.dto.request.JobRequest;
import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.CategoryResponse;
import com.jobportal.dto.response.JobDetailResponse;
import com.jobportal.dto.response.JobSummaryResponse;
import com.jobportal.dto.response.WorkModeResponse;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.JobService;

import jakarta.validation.Valid;

/**
 * Job REST controller.
 *
 * <h3>Design decisions</h3>
 *
 * <h4>POST /api/jobs/filter (not GET with body)</h4>
 * <p>HTTP GET with a request body is technically undefined by the HTTP spec and
 * is not supported by many HTTP clients, proxies, and load balancers. The filter
 * endpoint uses POST to carry the filter criteria in the request body.
 * The Pageable parameters (page, size, sort) are passed as query params
 * — handled automatically by Spring's {@code HandlerMethodArgumentResolver}.</p>
 *
 * <h4>Wildcard return types removed</h4>
 * <p>{@code ApiResponse<?>} breaks API documentation tools (OpenAPI/Swagger)
 * and client code generators. All endpoints now have fully typed returns.</p>
 *
 * <h4>createJob / updateJob return JobDetailResponse</h4>
 * <p>Returning the full detail after a write avoids a redundant follow-up GET
 * from the client. The cost is one extra re-fetch query in the service, which
 * is acceptable for write operations.</p>
 */
@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // ── Write Endpoints (Authenticated Recruiter) ─────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<JobDetailResponse>> createJob(
            @Valid @RequestBody JobRequest dto,
            Authentication authentication) throws JobPortalException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job posted successfully",
                        jobService.createJob(dto, authentication.getName())));
    }

    @PutMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobDetailResponse>> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody JobRequest dto,
            Authentication authentication) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success("Job updated successfully",
                jobService.updateJob(jobId, dto, authentication.getName())));
    }

    @DeleteMapping("/{jobId}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @PathVariable Long jobId,
            Authentication authentication) throws JobPortalException {
        jobService.deleteJob(jobId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("Job deleted successfully"));
    }

    // ── Recruiter: My Jobs ────────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getMyJobs(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable)
            throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                jobService.getMyJobs(authentication.getName(), pageable)));
    }

    // ── Public Read Endpoints ─────────────────────────────────────────────────

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getAllJobs(
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getAllJobs(pageable)));
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<ApiResponse<JobDetailResponse>> getJobById(
            @PathVariable Long jobId) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobById(jobId)));
    }

    /**
     * Increments view count and returns the full job detail.
     * Whitelisted as public in SecurityConfig.
     */
    @PostMapping("/{jobId}/view")
    public ResponseEntity<ApiResponse<JobDetailResponse>> viewJob(
            @PathVariable Long jobId) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(jobService.incrementViewAndGet(jobId)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> searchJobs(

            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) String city,

            @RequestParam(required = false) String state,

            @RequestParam(required = false) String country,

            @RequestParam(required = false) JobType jobType,

            @RequestParam(required = false) WorkingMode workingMode,

            @RequestParam(required = false) ExperienceLevel experienceLevel,

            @RequestParam(required = false) Long minimumSalary,

            @RequestParam(required = false) Long maximumSalary,

            @RequestParam(required = false) List<String> skills,

            @RequestParam(required = false) String category,

            @RequestParam(required = false) String qualification,

            @RequestParam(required = false) Boolean featured,

            @RequestParam(required = false) Boolean urgentHiring,

            @RequestParam(required = false) Boolean easyApply,

            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {
    	
    	{

            JobFilterRequest filter = new JobFilterRequest();

            filter.setKeyword(keyword);
            filter.setCity(city);
            filter.setState(state);
            filter.setCountry(country);
            filter.setJobType(jobType);
            filter.setWorkingMode(workingMode);
            filter.setExperienceLevel(experienceLevel);
            filter.setMinimumSalary(minimumSalary);
            filter.setMaximumSalary(maximumSalary);
            filter.setSkills(skills);
            filter.setCategory(category);
            filter.setQualification(qualification);
            filter.setFeatured(featured);
            filter.setUrgentHiring(urgentHiring);
            filter.setEasyApply(easyApply);

            return ResponseEntity.ok(
                    ApiResponse.success(
                            jobService.filterJobs(filter, pageable)
                    )
            );
        }
    }

    /**
     * Advanced filter endpoint. Uses POST (not GET) because complex filter
     * objects with lists (skills, etc.) are not reliably passed as GET query params.
     * Pageable (page, size, sort) is resolved from query params by Spring.
     *
     * Example: POST /api/jobs/filter?page=0&size=10&sort=createdAt,desc
     */
    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> filterJobs(
            @RequestBody(required = false) JobFilterRequest request,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        if (request == null) request = new JobFilterRequest();
        return ResponseEntity.ok(ApiResponse.success(
                jobService.filterJobs(request, pageable)));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getCompanyJobs(
            @PathVariable Long companyId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable)
            throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                jobService.getCompanyJobs(companyId, pageable)));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> getJobsByCategory(
            @PathVariable String category,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                jobService.getJobsByCategory(category, pageable)));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        jobService.getCategories()
                )
        );
    }
    @GetMapping("/work-modes")
    public ResponseEntity<ApiResponse<List<WorkModeResponse>>> getWorkModes() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        jobService.getWorkModes()
                )
        );
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<JobSummaryResponse>>> latestJobs() {
        return ResponseEntity.ok(ApiResponse.success(jobService.latestJobs()));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<Page<JobSummaryResponse>>> featuredJobs(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(jobService.featuredJobs(pageable)));
    }

    @GetMapping("/{jobId}/similar")
    public ResponseEntity<ApiResponse<List<JobSummaryResponse>>> similarJobs(
            @PathVariable Long jobId) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(jobService.similarJobs(jobId)));
    }
}
