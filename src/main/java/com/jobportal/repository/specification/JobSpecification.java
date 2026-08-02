package com.jobportal.repository.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.jobportal.domain.JobStatus;
import com.jobportal.dto.request.JobFilterRequest;
import com.jobportal.entity.Job;

import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;

public class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> buildFrom(JobFilterRequest request) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ------------------------------------------------------------------
            // Only OPEN jobs
            // ------------------------------------------------------------------

            predicates.add(
                    cb.equal(root.get("status"), JobStatus.OPEN)
            );

            // ------------------------------------------------------------------
            // Keyword Search
            // ------------------------------------------------------------------

            if (hasText(request.getKeyword())) {

                String pattern = likePattern(request.getKeyword());

                predicates.add(

                        cb.or(

                                cb.like(cb.lower(root.get("jobTitle")), pattern),

                                cb.like(cb.lower(root.get("category")), pattern),

                                cb.like(cb.lower(root.get("description")), pattern),

                                cb.like(cb.lower(root.get("requirements")), pattern),

                                cb.like(cb.lower(root.get("qualification")), pattern),

                                cb.like(cb.lower(root.get("city")), pattern),

                                cb.like(cb.lower(root.get("state")), pattern)

                        )

                );

            }

            // ------------------------------------------------------------------
            // Skills (Production-safe using EXISTS)
            // ------------------------------------------------------------------

            if (request.getSkills() != null && !request.getSkills().isEmpty()) {

                for (String skill : request.getSkills()) {

                    Subquery<Long> subQuery = query.subquery(Long.class);

                    var job = subQuery.from(Job.class);

                    Expression<String> skillExpression =
                            job.join("skillsRequired")
                                    .as(String.class);

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

            // ------------------------------------------------------------------
            // Category
            // ------------------------------------------------------------------

            if (hasText(request.getCategory())) {

                predicates.add(

                        cb.like(

                                cb.lower(root.get("category")),

                                likePattern(request.getCategory())

                        )

                );

            }

            // ------------------------------------------------------------------
            // Location
            // ------------------------------------------------------------------

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

            // ------------------------------------------------------------------
            // Job Type
            // ------------------------------------------------------------------

            if (request.getJobType() != null) {

                predicates.add(

                        cb.equal(
                                root.get("jobType"),
                                request.getJobType()
                        )

                );

            }

            // ------------------------------------------------------------------
            // Working Mode
            // ------------------------------------------------------------------

            if (request.getWorkingMode() != null) {

                predicates.add(

                        cb.equal(

                                root.get("workingMode"),

                                request.getWorkingMode()

                        )

                );

            }

            // ------------------------------------------------------------------
            // Experience Level
            // ------------------------------------------------------------------

            if (request.getExperienceLevel() != null) {

                predicates.add(

                        cb.equal(

                                root.get("experienceLevel"),

                                request.getExperienceLevel()

                        )

                );

            }

            // ------------------------------------------------------------------
            // Experience Range
            // ------------------------------------------------------------------

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

            // ------------------------------------------------------------------
            // Salary
            // ------------------------------------------------------------------

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

            // ------------------------------------------------------------------
            // Qualification
            // ------------------------------------------------------------------

            if (hasText(request.getQualification())) {

                predicates.add(

                        cb.like(

                                cb.lower(root.get("qualification")),

                                likePattern(request.getQualification())

                        )

                );

            }

            // ------------------------------------------------------------------
            // Featured
            // ------------------------------------------------------------------

            if (Boolean.TRUE.equals(request.getFeatured())) {

                predicates.add(

                        cb.isTrue(root.get("featured"))

                );

            }

            // ------------------------------------------------------------------
            // Urgent Hiring
            // ------------------------------------------------------------------

            if (Boolean.TRUE.equals(request.getUrgentHiring())) {

                predicates.add(

                        cb.isTrue(root.get("urgentHiring"))

                );

            }

            // ------------------------------------------------------------------
            // Easy Apply
            // ------------------------------------------------------------------

            if (Boolean.TRUE.equals(request.getEasyApply())) {

                predicates.add(

                        cb.isTrue(root.get("easyApply"))

                );

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