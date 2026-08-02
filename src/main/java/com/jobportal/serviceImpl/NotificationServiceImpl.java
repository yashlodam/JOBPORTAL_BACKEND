package com.jobportal.serviceImpl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.domain.NotificationType;
import com.jobportal.dto.response.NotificationResponse;
import com.jobportal.dto.response.UnreadCountResponse;
import com.jobportal.entity.Notification;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.NotificationRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;

/**
 * Implements notification persistence and retrieval.
 *
 * <p>The {@code send} method is {@code @Async}-safe — it only writes to the DB
 * and does not call any external service, making it safe to call from within
 * an already-open transaction (the notification is committed with the parent tx).
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    // ── Creation ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void send(User recipient, NotificationType type,
                     String title, String message,
                     Long referenceId, String referenceType) {
        Notification notification = new Notification(
                recipient, type, title, message, referenceId, referenceType);
        notificationRepository.save(notification);
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(String email, Pageable pageable)
            throws JobPortalException {
        User user = findUserByEmail(email);
        return notificationRepository
                .findByRecipientId(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyUnreadNotifications(String email, Pageable pageable)
            throws JobPortalException {
        User user = findUserByEmail(email);
        return notificationRepository
                .findUnreadByRecipientId(user.getId(), pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(String email) throws JobPortalException {
        User user = findUserByEmail(email);
        long count = notificationRepository.countUnreadByRecipientId(user.getId());
        return new UnreadCountResponse(count);
    }

    // ── Mutation ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String email) throws JobPortalException {
        User user = findUserByEmail(email);
        // markOneRead only updates if the notification belongs to this user — no IDOR possible
        notificationRepository.markOneRead(notificationId, user.getId());
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) throws JobPortalException {
        User user = findUserByEmail(email);
        notificationRepository.markAllReadForUser(user.getId());
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, String email) throws JobPortalException {
        User user = findUserByEmail(email);
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> JobPortalException.notFound(
                        "Notification not found with id: " + notificationId));

        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw JobPortalException.forbidden(
                    "You are not authorized to delete this notification.");
        }

        notificationRepository.delete(notification);
    }

    @Override
    @Transactional
    public void deleteAllNotifications(String email) throws JobPortalException {
        User user = findUserByEmail(email);
        notificationRepository.deleteAllByRecipientId(user.getId());
    }

    // ── Private Helpers ─────────────────────────────────────────────────────

    private User findUserByEmail(String email) throws JobPortalException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found"));
    }

    private NotificationResponse toResponse(Notification n) {
        NotificationResponse dto = new NotificationResponse();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setReferenceId(n.getReferenceId());
        dto.setReferenceType(n.getReferenceType());
        dto.setRead(n.isRead());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
