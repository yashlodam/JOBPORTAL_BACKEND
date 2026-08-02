package com.jobportal.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.dto.response.ApiResponse;
import com.jobportal.dto.response.NotificationResponse;
import com.jobportal.dto.response.UnreadCountResponse;
import com.jobportal.exception.JobPortalException;
import com.jobportal.service.NotificationService;

/**
 * Notification controller — all endpoints require authentication.
 * Users can only access their own notifications.
 *
 * <p>Routes:
 * <pre>
 *   GET    /api/notifications                   — paginated feed (all)
 *   GET    /api/notifications/unread            — paginated unread feed
 *   GET    /api/notifications/unread-count      — badge count
 *   PATCH  /api/notifications/{id}/read         — mark one as read
 *   PATCH  /api/notifications/read-all          — mark all as read
 *   DELETE /api/notifications/{id}              — delete one
 *   DELETE /api/notifications                   — delete all
 * </pre>
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    /**
     * Full notification feed — all notifications, newest-first.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getAll(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable)
            throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyNotifications(authentication.getName(), pageable)));
    }

    /**
     * Only unread notifications — useful for the dropdown panel.
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnread(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable)
            throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getMyUnreadNotifications(authentication.getName(), pageable)));
    }

    /**
     * Lightweight count endpoint — poll this to drive the bell badge.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            Authentication authentication) throws JobPortalException {
        return ResponseEntity.ok(ApiResponse.success(
                notificationService.getUnreadCount(authentication.getName())));
    }

    // ── Mark Read ────────────────────────────────────────────────────────────

    /**
     * Mark a single notification as read.
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) throws JobPortalException {
        notificationService.markAsRead(notificationId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("Notification marked as read"));
    }

    /**
     * Mark all notifications as read in one call.
     */
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            Authentication authentication) throws JobPortalException {
        notificationService.markAllAsRead(authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("All notifications marked as read"));
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    /**
     * Delete a single notification.
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteOne(
            @PathVariable Long notificationId,
            Authentication authentication) throws JobPortalException {
        notificationService.deleteNotification(notificationId, authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("Notification deleted"));
    }

    /**
     * Clear the entire notification inbox.
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAll(
            Authentication authentication) throws JobPortalException {
        notificationService.deleteAllNotifications(authentication.getName());
        return ResponseEntity.ok(ApiResponse.message("All notifications cleared"));
    }
}
