package com.jobportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobportal.entity.Company;
import com.jobportal.entity.Job;
import com.jobportal.entity.Recruiter;

public interface JobRepository extends JpaRepository<Job, Long>{

	List<Job> findByCompany(Company company);

	List<Job> findByRecruiter(Recruiter recruiter);
	
	List<Job> findByCompanyId(Long companyId);
	
	@Query("""
		    SELECT DISTINCT j
		    FROM Job j
		    LEFT JOIN j.skillsRequired sr
		    LEFT JOIN j.preferredSkills ps
		    WHERE
		        LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(j.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(j.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(j.state) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(j.country) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(j.qualification) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(sr) LIKE LOWER(CONCAT('%', :keyword, '%'))
		        OR LOWER(ps) LIKE LOWER(CONCAT('%', :keyword, '%'))
		    """)
		List<Job> searchJobs(@Param("keyword") String keyword);
	
	
	
	@Query("""
		    SELECT j FROM Job j
		    WHERE j.id <> :jobId
		    AND j.status = com.jobportal.domain.JobStatus.ACTIVE
		    AND (
		            LOWER(j.category) = LOWER(:category)
		         OR LOWER(j.city) = LOWER(:city)
		         OR LOWER(j.jobTitle) LIKE LOWER(CONCAT('%', :jobTitle, '%'))
		    )
		    ORDER BY j.postedOn DESC
		    """)
		List<Job> findSimilarJobs(
		        @Param("jobId") Long jobId,
		        @Param("category") String category,
		        @Param("city") String city,
		        @Param("jobTitle") String jobTitle);
	
	
	
	@Query("""
		    SELECT j FROM Job j
		    WHERE
		        (:city IS NULL OR :city = '' OR LOWER(j.city) = LOWER(:city))
		    AND (:jobType IS NULL OR CAST(j.jobType AS string) = :jobType)
		    AND (:workingMode IS NULL OR CAST(j.workingMode AS string) = :workingMode)
		    AND (:experienceLevel IS NULL OR CAST(j.experienceLevel AS string) = :experienceLevel)
		    AND (:minimumSalary IS NULL OR j.minimumSalary >= :minimumSalary)
		    AND (:maximumSalary IS NULL OR j.maximumSalary <= :maximumSalary)
		    """)
		List<Job> filterJobs(
		        @Param("city") String city,
		        @Param("jobType") String jobType,
		        @Param("workingMode") String workingMode,
		        @Param("experienceLevel") String experienceLevel,
		        @Param("minimumSalary") Long minimumSalary,
		        @Param("maximumSalary") Long maximumSalary);

	List<Job> findByCategoryIgnoreCase(String category);
}
