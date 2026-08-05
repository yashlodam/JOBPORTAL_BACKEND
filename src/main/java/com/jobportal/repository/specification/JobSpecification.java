package com.jobportal.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.jobportal.domain.JobStatus;
import com.jobportal.dto.request.JobFilterRequest;
import com.jobportal.entity.Company;
import com.jobportal.entity.Job;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

public class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> buildFrom(JobFilterRequest request) {

        return (root, query, cb) -> {

            query.distinct(true);

            List<Predicate> predicates = new ArrayList<>();

            // --------------------------------------------------------
            // Only OPEN jobs
            // --------------------------------------------------------

            predicates.add(cb.equal(root.get("status"), JobStatus.OPEN));

            // --------------------------------------------------------
            // Keyword Search
            // --------------------------------------------------------

            if (hasText(request.getKeyword())) {

                String keyword = likePattern(request.getKeyword());

                List<Predicate> keywordPredicates = new ArrayList<>();

                keywordPredicates.add(cb.like(cb.lower(root.get("jobTitle")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("category")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("description")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("requirements")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("responsibilities")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("aboutRole")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("benefits")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("qualification")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("city")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("state")), keyword));
                keywordPredicates.add(cb.like(cb.lower(root.get("country")), keyword));

                // Company Name
                Join<Job, Company> companyJoin = root.join("company");
                keywordPredicates.add(
                        cb.like(
                                cb.lower(companyJoin.get("companyName")),
                                keyword));

                // Required Skills
                Subquery<Long> requiredSkillQuery = query.subquery(Long.class);
                var requiredJob = requiredSkillQuery.from(Job.class);

                Expression<String> requiredSkill =
                        requiredJob.join("skillsRequired").as(String.class);

                requiredSkillQuery.select(cb.literal(1L));

                requiredSkillQuery.where(
                        cb.equal(requiredJob.get("id"), root.get("id")),
                        cb.like(cb.lower(requiredSkill), keyword)
                );

                keywordPredicates.add(cb.exists(requiredSkillQuery));

                // Preferred Skills
                Subquery<Long> preferredSkillQuery = query.subquery(Long.class);
                var preferredJob = preferredSkillQuery.from(Job.class);

                Expression<String> preferredSkill =
                        preferredJob.join("preferredSkills").as(String.class);

                preferredSkillQuery.select(cb.literal(1L));

                preferredSkillQuery.where(
                        cb.equal(preferredJob.get("id"), root.get("id")),
                        cb.like(cb.lower(preferredSkill), keyword)
                );

                keywordPredicates.add(cb.exists(preferredSkillQuery));

                predicates.add(
                        cb.or(keywordPredicates.toArray(new Predicate[0]))
                );
            }

            // --------------------------------------------------------
            // Category
            // --------------------------------------------------------

            if (hasText(request.getCategory())) {

                predicates.add(
                        cb.like(
                                cb.lower(root.get("category")),
                                likePattern(request.getCategory())
                        )
                );
            }
            
            if (hasText(request.getCompanyName())) {

                Join<Job, Company> companyJoin = root.join("company");

                predicates.add(
                        cb.like(
                                cb.lower(companyJoin.get("companyName")),
                                likePattern(request.getCompanyName())
                        )
                );
            }

            // --------------------------------------------------------
            // Location
            // --------------------------------------------------------

            if (hasText(request.getCity())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("city")),
                                likePattern(request.getCity())
                        )
                );
            }

            if (hasText(request.getState())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("state")),
                                likePattern(request.getState())
                        )
                );
            }

            if (hasText(request.getCountry())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("country")),
                                likePattern(request.getCountry())
                        )
                );
            }

            // --------------------------------------------------------
            // Job Type
            // --------------------------------------------------------

            if (request.getJobType() != null) {
                predicates.add(cb.equal(root.get("jobType"), request.getJobType()));
            }

            // --------------------------------------------------------
            // Working Mode
            // --------------------------------------------------------

            if (request.getWorkingMode() != null) {
                predicates.add(cb.equal(root.get("workingMode"), request.getWorkingMode()));
            }

            // --------------------------------------------------------
            // Experience Level
            // --------------------------------------------------------

            if (request.getExperienceLevel() != null) {
                predicates.add(cb.equal(root.get("experienceLevel"), request.getExperienceLevel()));
            }

            // --------------------------------------------------------
            // Experience Range
            // --------------------------------------------------------

            if (request.getMinimumExperience() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("minimumExperience"),
                                request.getMinimumExperience()
                        )
                );
            }

            if (request.getMaximumExperience() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("maximumExperience"),
                                request.getMaximumExperience()
                        )
                );
            }

            // --------------------------------------------------------
            // Salary
            // --------------------------------------------------------

            if (request.getMinimumSalary() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("minimumSalary"),
                                request.getMinimumSalary()
                        )
                );
            }

            if (request.getMaximumSalary() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("maximumSalary"),
                                request.getMaximumSalary()
                        )
                );
            }

            // --------------------------------------------------------
            // Skills Filter
            // --------------------------------------------------------

            if (request.getSkills() != null && !request.getSkills().isEmpty()) {

                for (String skill : request.getSkills()) {

                    Subquery<Long> subQuery = query.subquery(Long.class);

                    var job = subQuery.from(Job.class);

                    Expression<String> skillExpression =
                            job.join("skillsRequired").as(String.class);

                    subQuery.select(cb.literal(1L));

                    subQuery.where(
                            cb.equal(job.get("id"), root.get("id")),
                            cb.like(
                                    cb.lower(skillExpression),
                                    likePattern(skill)
                            )
                    );

                    predicates.add(cb.exists(subQuery));
                }
            }

            // --------------------------------------------------------
            // Qualification
            // --------------------------------------------------------

            if (hasText(request.getQualification())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("qualification")),
                                likePattern(request.getQualification())
                        )
                );
            }

            // --------------------------------------------------------
            // Featured
            // --------------------------------------------------------

            if (Boolean.TRUE.equals(request.getFeatured())) {
                predicates.add(cb.isTrue(root.get("featured")));
            }

            // --------------------------------------------------------
            // Urgent Hiring
            // --------------------------------------------------------

            if (Boolean.TRUE.equals(request.getUrgentHiring())) {
                predicates.add(cb.isTrue(root.get("urgentHiring")));
            }

            // --------------------------------------------------------
            // Easy Apply
            // --------------------------------------------------------

            if (Boolean.TRUE.equals(request.getEasyApply())) {
                predicates.add(cb.isTrue(root.get("easyApply")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String likePattern(String value) {
        return "%" + value.trim().toLowerCase() + "%";
    }
}