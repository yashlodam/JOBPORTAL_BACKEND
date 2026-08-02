package com.jobportal.entity;

import com.jobportal.domain.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Persisted in-app notification.
 * Each row targets one recipient user. Bulk events create one row per recipient.
 *
 * <p>referenceId + referenceType together form a soft foreign-key to the
 * triggering domain entity (e.g. a JobApplication or a Job) so the frontend
 * can deep-link directly to the relevant resource.
 */
@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notification_recipient", columnList = "recipient_id"),
        @Index(name = "idx_notification_read",      columnList = "recipient_id, is_read"),
        @Index(name = "idx_notification_created",   columnList = "recipient_id, created_at")
    }
)
public class Notification extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The user who receives this notification. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    /** Category of the event — used for icons, routing, and filtering. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    /** Short heading shown in the notification bell. */
    @Column(nullable = false, length = 150)
    private String title;

    /** Full detail message shown when the notification is expanded. */
    @Column(nullable = false, length = 500)
    private String message;

    /**
     * Optional ID of the triggering domain object.
     * Combined with referenceType it forms a soft FK so the frontend can link to it.
     */
    @Column(name = "reference_id")
    private Long referenceId;

    /**
     * Discriminator string for referenceId: "JOB", "APPLICATION", "COMPANY", etc.
     */
    @Column(name = "reference_type", length = 30)
    private String referenceType;

    /** False until the user reads / dismisses the notification. */
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    public Notification() {}

    // ── Convenience Constructor ──────────────────────────────────────────────

    public Notification(User recipient, NotificationType type,
                        String title, String message,
                        Long referenceId, String referenceType) {
        this.recipient     = recipient;
        this.type          = type;
        this.title         = title;
        this.message       = message;
        this.referenceId   = referenceId;
        this.referenceType = referenceType;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }

    public String getReferenceType() { return referenceType; }
    public void setReferenceType(String referenceType) { this.referenceType = referenceType; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}
