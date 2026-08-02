package com.jobportal.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jobportal.entity.Notification;

/**
 * Repository for Notification entities.
 * All queries are scoped to a single recipient to prevent data leaks.
 */
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** Paginated notification feed for a user, newest-first. */
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId ORDER BY n.createdAt DESC")
    Page<Notification> findByRecipientId(@Param("userId") Long userId, Pageable pageable);

    /** Paginated unread notifications for a user. */
    @Query("SELECT n FROM Notification n WHERE n.recipient.id = :userId AND n.read = false ORDER BY n.createdAt DESC")
    Page<Notification> findUnreadByRecipientId(@Param("userId") Long userId, Pageable pageable);

    /** Count of unread notifications — used for the bell badge. */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient.id = :userId AND n.read = false")
    long countUnreadByRecipientId(@Param("userId") Long userId);

    /** Mark all notifications for a user as read in one UPDATE statement. */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.recipient.id = :userId AND n.read = false")
    void markAllReadForUser(@Param("userId") Long userId);

    /** Mark one specific notification as read (only if it belongs to the given user). */
    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.id = :notificationId AND n.recipient.id = :userId")
    void markOneRead(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    /** Delete all notifications for a user (account cleanup). */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.recipient.id = :userId")
    void deleteAllByRecipientId(@Param("userId") Long userId);
}
