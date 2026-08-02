package com.jobportal.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jobportal.domain.NotificationType;
import com.jobportal.dto.response.NotificationResponse;
import com.jobportal.dto.response.UnreadCountResponse;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;

/**
 * Service for creating and managing in-app notifications.
 *
 * <p>The {@code send} method is the single creation point — all other services
 * call it to emit notifications without knowing the persistence details.
 */
public interface NotificationService {

    // ── Creation ─────────────────────────────────────────────────────────────

    /**
     * Persist a notification for a single recipient.
     *
     * @param recipient     the target user
     * @param type          event category
     * @param title         short heading (max 150 chars)
     * @param message       detailed description (max 500 chars)
     * @param referenceId   optional ID of the triggering entity (nullable)
     * @param referenceType optional type discriminator e.g. "JOB", "APPLICATION" (nullable)
     */
    void send(User recipient, NotificationType type,
              String title, String message,
              Long referenceId, String referenceType);

    // ── Read ─────────────────────────────────────────────────────────────────

    /** Paginated notification feed for the authenticated user (all, newest-first). */
    Page<NotificationResponse> getMyNotifications(String email, Pageable pageable)
            throws JobPortalException;

    /** Paginated feed of only unread notifications. */
    Page<NotificationResponse> getMyUnreadNotifications(String email, Pageable pageable)
            throws JobPortalException;

    /** Returns the count of unread notifications (for the bell badge). */
    UnreadCountResponse getUnreadCount(String email) throws JobPortalException;

    // ── Mutation ─────────────────────────────────────────────────────────────

    /** Mark one notification as read (only if it belongs to the caller). */
    void markAsRead(Long notificationId, String email) throws JobPortalException;

    /** Mark every notification for the caller as read in one statement. */
    void markAllAsRead(String email) throws JobPortalException;

    /** Delete one notification (only if it belongs to the caller). */
    void deleteNotification(Long notificationId, String email) throws JobPortalException;

    /** Delete all notifications for the caller. */
    void deleteAllNotifications(String email) throws JobPortalException;
}
