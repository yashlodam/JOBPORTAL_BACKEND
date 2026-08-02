package com.jobportal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobportal.entity.Profile;

/**
 * Profile repository.
 *
 * <h3>Loading Strategy</h3>
 * <p>Two distinct query methods serve two distinct purposes:</p>
 *
 * <ol>
 *   <li>{@link #findByUserEmail} — lightweight, no joins. Used exclusively
 *       for write (mutation) paths where only scalar fields are needed.</li>
 *   <li>{@link #findByUserEmailWithDetails} — loads every association that
 *       {@code ProfileResponse} needs via a single set of efficient queries.
 *       Used in every code path that calls {@code toResponse()}.</li>
 * </ol>
 *
 * <h3>Why NOT a single JOIN FETCH for all collections?</h3>
 * <p>Joining multiple {@code @OneToMany} / {@code @ElementCollection} bags in
 * one JPQL query causes a Cartesian product: N experiences × M educations × …
 * rows returned — correct but massively wasteful. Hibernate 6 solves this with
 * the {@code @BatchSize} strategy: the root entity is fetched first, then each
 * lazy collection is fetched in a single batch query per collection type,
 * costing exactly 1 + 5 = 6 SQL statements regardless of how many profiles
 * are loaded in a page. See {@code application.properties}.</p>
 *
 * <h3>Why @EntityGraph only for user and resume?</h3>
 * <p>{@code user} and {@code resume} are {@code @OneToOne} — one extra column
 * each, safely joined without row multiplication. The {@code @ElementCollection}
 * and {@code @OneToMany} bags are delegated to {@code @BatchSize}.</p>
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * Lightweight lookup — no joins, scalar fields only.
     * Use this in write paths when you only need to mutate and save.
     */
    @Query("SELECT p FROM Profile p JOIN p.user u WHERE u.email = :email")
    Optional<Profile> findByUserEmail(@Param("email") String email);

    /**
     * Full-detail lookup — loads every association needed by ProfileResponse.
     *
     * <ul>
     *   <li>user  — joined via EntityGraph (@OneToOne — no row multiplication)</li>
     *   <li>resume — joined via EntityGraph (@OneToOne — no row multiplication)</li>
     *   <li>skills, languages, experiences, educations, certifications —
     *       loaded by Hibernate's BatchSize mechanism (see application.properties).
     *       Each fires one additional SQL, for a constant total of 6 queries.</li>
     * </ul>
     *
     * <p>Always use this method before calling {@code toResponse()}.</p>
     */
    @EntityGraph(attributePaths = {"user", "resume"})
    @Query("SELECT p FROM Profile p JOIN p.user u WHERE u.email = :email")
    Optional<Profile> findByUserEmailWithDetails(@Param("email") String email);

    /** Lookup by user PK — used in registration flow. */
    Optional<Profile> findByUserId(Long userId);
}
