package com.jobportal.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.jobportal.domain.ApplicationStatus;
import com.jobportal.domain.NotificationPriority;
import com.jobportal.domain.NotificationType;
import com.jobportal.event.ApplicationStatusChangedEvent;
import com.jobportal.event.ApplicationSubmittedEvent;
import com.jobportal.event.ApplicationWithdrawnEvent;
import com.jobportal.event.CompanyProfileUpdatedEvent;
import com.jobportal.event.JobDeletedEvent;
import com.jobportal.event.JobPostedEvent;
import com.jobportal.event.PasswordResetEvent;
import com.jobportal.event.ProfileCompletedEvent;
import com.jobportal.event.UserRegisteredEvent;
import com.jobportal.repository.NotificationRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.NotificationService;

/**
 * Spring event listener that translates domain events into in-app notifications.
 *
 * <h3>Decoupling</h3>
 * <p>Job, Application, and other modules publish events via
 * {@link org.springframework.context.ApplicationEventPublisher}. They have
 * zero dependency on {@link NotificationService}. This listener is the
 * single integration point between the domain modules and the notification module.</p>
 *
 * <h3>Transaction phase</h3>
 * <p>All listeners use {@code @TransactionalEventListener(AFTER_COMMIT)}.
 * This means notifications are only created AFTER the triggering transaction
 * commits successfully. If the business operation rolls back (e.g. a duplicate
 * application), no spurious notification is created. Each listener method runs
 * in its own new transaction (via the {@code @Transactional} on
 * {@link NotificationService#send}).</p>
 *
 * <h3>Failure isolation</h3>
 * <p>A notification failure does NOT roll back the original business transaction
 * because the listener runs after commit. Errors are logged and swallowed
 * (to be replaced by a retry queue / dead-letter topic in a future iteration).</p>
 */
@Component
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService      notificationService;
    private final UserRepository           userRepository;
    private final NotificationRepository   notificationRepository;

    public NotificationEventListener(NotificationService notificationService,
                                     UserRepository userRepository,
                                     NotificationRepository notificationRepository) {
        this.notificationService    = notificationService;
        this.userRepository         = userRepository;
        this.notificationRepository = notificationRepository;
    }

    // ── Job Events ───────────────────────────────────────────────────────────

    /**
     * Handles {@link JobPostedEvent}.
     *
     * <p>Currently logs the event for audit purposes. In a future iteration this
     * will fan-out {@code NEW_JOB} / {@code FEATURED_JOB} notifications to users
     * whose skills / preferences match the posted job (AI matching module).</p>
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onJobPosted(JobPostedEvent event) {
        try {
            NotificationType type = Boolean.TRUE.equals(event.getJob().getFeatured())
                    ? NotificationType.FEATURED_JOB
                    : NotificationType.NEW_JOB;

            log.info("Job posted — type=[{}] jobId=[{}] recruiter=[{}]",
                    type, event.getJob().getId(), event.getRecruiterUser().getEmail());

            // TODO: fan-out to matched users via AI recommendation engine
            // jobMatchService.findMatchedUsers(event.getJob())
            //     .forEach(user -> notificationService.send(user, JOB_MATCH, ...));

        } catch (Exception ex) {
            log.error("Failed to handle JobPostedEvent for job [{}]: {}",
                    event.getJob().getId(), ex.getMessage(), ex);
        }
    }

    // ── Application Events ───────────────────────────────────────────────────

    /**
     * Notifies the recruiter when a new application is submitted.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationSubmitted(ApplicationSubmittedEvent event) {
        try {
            notificationService.send(
                    event.getRecruiterUser(),
                    NotificationType.APPLICATION_RECEIVED,
                    NotificationPriority.MEDIUM,
                    "New Application Received",
                    event.getApplicant().getName()
                            + " applied for \"" + event.getJob().getJobTitle() + "\".",
                    "/recruiter/applications/" + event.getApplicationId(),
                    event.getApplicationId(),
                    "APPLICATION"
            );
            log.debug("APPLICATION_RECEIVED notification sent — applicationId=[{}]",
                    event.getApplicationId());
        } catch (Exception ex) {
            log.error("Failed to notify recruiter for ApplicationSubmittedEvent [{}]: {}",
                    event.getApplicationId(), ex.getMessage(), ex);
        }
    }

    /**
     * Notifies the applicant when their application status changes.
     * Maps the raw {@link ApplicationStatus} to the most specific {@link NotificationType}.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationStatusChanged(ApplicationStatusChangedEvent event) {
        try {
            ApplicationStatus newStatus   = event.getNewStatus();
            NotificationType  type        = mapStatusToType(newStatus);
            NotificationPriority priority = mapStatusToPriority(newStatus);
            String jobTitle = event.getApplication().getJob().getJobTitle();
            Long appId      = event.getApplication().getId();

            notificationService.send(
                    event.getApplication().getApplicant(),
                    type,
                    priority,
                    buildStatusTitle(newStatus),
                    "Your application for \"" + jobTitle + "\" is now: "
                            + formatStatus(newStatus.name()) + ".",
                    "/applications/" + appId,
                    appId,
                    "APPLICATION"
            );
            log.debug("Status-change notification sent — applicationId=[{}] newStatus=[{}]",
                    appId, newStatus);
        } catch (Exception ex) {
            log.error("Failed to notify applicant for ApplicationStatusChangedEvent: {}",
                    ex.getMessage(), ex);
        }
    }

    /**
     * Notifies the recruiter when a candidate withdraws their application.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onApplicationWithdrawn(ApplicationWithdrawnEvent event) {
        try {
            userRepository.findById(event.getRecruiterUserId()).ifPresent(recruiterUser ->
                    notificationService.send(
                            recruiterUser,
                            NotificationType.APPLICATION_WITHDRAWN,
                            NotificationPriority.LOW,
                            "Application Withdrawn",
                            event.getApplicantName()
                                    + " withdrew their application for \""
                                    + event.getJobTitle() + "\".",
                            "/recruiter/jobs/" + event.getJobId(),
                            event.getApplicationId(),
                            "APPLICATION"
                    )
            );
            log.debug("APPLICATION_WITHDRAWN notification sent — applicationId=[{}]",
                    event.getApplicationId());
        } catch (Exception ex) {
            log.error("Failed to notify recruiter for ApplicationWithdrawnEvent [{}]: {}",
                    event.getApplicationId(), ex.getMessage(), ex);
        }
    }

    // ── Private Helpers ──────────────────────────────────────────────────────

    private NotificationType mapStatusToType(ApplicationStatus status) {
        return switch (status) {
            case SHORTLISTED  -> NotificationType.APPLICATION_SHORTLISTED;
            case REJECTED     -> NotificationType.APPLICATION_REJECTED;
            case INTERVIEWING -> NotificationType.INTERVIEW_SCHEDULED;
            case OFFERED      -> NotificationType.OFFER_RECEIVED;
            default           -> NotificationType.APPLICATION_STATUS_UPDATED;
        };
    }

    private NotificationPriority mapStatusToPriority(ApplicationStatus status) {
        return switch (status) {
            case SHORTLISTED, INTERVIEWING -> NotificationPriority.HIGH;
            case OFFERED                   -> NotificationPriority.HIGH;
            case REJECTED                  -> NotificationPriority.MEDIUM;
            default                        -> NotificationPriority.LOW;
        };
    }

    private String buildStatusTitle(ApplicationStatus status) {
        return switch (status) {
            case SHORTLISTED  -> "Congratulations! You've been Shortlisted";
            case REJECTED     -> "Application Update";
            case INTERVIEWING -> "Interview Scheduled!";
            case OFFERED      -> "Offer Received!";
            default           -> "Application Status Updated";
        };
    }

    /**
     * Converts an enum constant name to a human-readable label.
     * e.g. {@code "INTERVIEW_SCHEDULED"} → {@code "Interview Scheduled"}
     */
    private String formatStatus(String status) {
        String lower = status.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    // ── User / Auth Events ───────────────────────────────────────────────────

    /**
     * Sends a welcome {@code ACCOUNT} notification when a new user registers.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserRegistered(UserRegisteredEvent event) {
        try {
            String roleLabel = switch (event.getUser().getAccountType()) {
                case APPLICANT -> "job seeker";
                case EMPLOYER  -> "recruiter";
                default        -> "member";
            };
            notificationService.send(
                    event.getUser(),
                    NotificationType.ACCOUNT,
                    NotificationPriority.LOW,
                    "Welcome to Velora! 🎉",
                    "Hi " + event.getUser().getName() + "! Your " + roleLabel
                            + " account is ready. Complete your profile to get started.",
                    "/profile",
                    null,
                    null
            );
            log.debug("Welcome notification sent to [{}]", event.getUser().getEmail());
        } catch (Exception ex) {
            log.error("Failed to send welcome notification for [{}]: {}",
                    event.getUser().getEmail(), ex.getMessage(), ex);
        }
    }

    /**
     * Sends a {@code SECURITY} alert when a user resets their password.
     * This is a standard security best-practice (LinkedIn, Google, GitHub).
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onPasswordReset(PasswordResetEvent event) {
        try {
            notificationService.send(
                    event.getUser(),
                    NotificationType.SECURITY,
                    NotificationPriority.HIGH,
                    "Password Changed Successfully",
                    "Your Velora account password was just changed. "
                            + "If you did not make this change, contact support immediately.",
                    "/settings/security",
                    null,
                    null
            );
            log.debug("SECURITY notification sent for password reset [{}]",
                    event.getUser().getEmail());
        } catch (Exception ex) {
            log.error("Failed to send password-reset SECURITY notification for [{}]: {}",
                    event.getUser().getEmail(), ex.getMessage(), ex);
        }
    }

    // ── Company Events ───────────────────────────────────────────────────────

    /**
     * Notifies the recruiter when they create or update their company profile.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCompanyProfileUpdated(CompanyProfileUpdatedEvent event) {
        try {
            boolean isNew = event.isCreated();
            notificationService.send(
                    event.getRecruiterUser(),
                    NotificationType.COMPANY_UPDATE,
                    NotificationPriority.LOW,
                    isNew ? "Company Profile Created! 🏢"
                          : "Company Profile Updated",
                    isNew ? "Your company \"" + event.getCompany().getCompanyName()
                                    + "\" is live. You can now post jobs!"
                          : "Your company \"" + event.getCompany().getCompanyName()
                                    + "\" profile has been updated successfully.",
                    "/company/" + event.getCompany().getId(),
                    event.getCompany().getId(),
                    "COMPANY"
            );
            log.debug("COMPANY_UPDATE notification sent to [{}] for company [{}]",
                    event.getRecruiterUser().getEmail(), event.getCompany().getId());
        } catch (Exception ex) {
            log.error("Failed to send company notification for [{}]: {}",
                    event.getRecruiterUser().getEmail(), ex.getMessage(), ex);
        }
    }

    // ── Profile Events ───────────────────────────────────────────────────────

    /**
     * Sends a one-time {@code PROFILE_COMPLETED} celebration notification.
     *
     * <p>De-duplication: the notification is only created if the user has never
     * received a {@code PROFILE_COMPLETED} notification before. This prevents
     * the celebration from firing every time the user edits their already-complete
     * profile.</p>
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onProfileCompleted(ProfileCompletedEvent event) {
        try {
            // De-duplicate: skip if a PROFILE_COMPLETED notification already exists
            boolean alreadyNotified = notificationRepository
                    .existsByRecipientIdAndType(
                            event.getUser().getId(), NotificationType.PROFILE_COMPLETED);
            if (alreadyNotified) {
                return;
            }

            notificationService.send(
                    event.getUser(),
                    NotificationType.PROFILE_COMPLETED,
                    NotificationPriority.MEDIUM,
                    "Your Profile is 100% Complete! ⭐",
                    "Great job! A complete profile gets 5x more recruiter views. "
                            + "Keep it updated to stay visible.",
                    "/profile",
                    null,
                    null
            );
            log.info("PROFILE_COMPLETED notification sent to [{}]",
                    event.getUser().getEmail());
        } catch (Exception ex) {
            log.error("Failed to send profile-completed notification for [{}]: {}",
                    event.getUser().getEmail(), ex.getMessage(), ex);
        }
    }

    // ── Job Deletion Events ──────────────────────────────────────────────────

    /**
     * Notifies all applicants when a job they applied to has been deleted.
     * Sends one {@code JOB_EXPIRED} notification per applicant.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onJobDeleted(JobDeletedEvent event) {
        if (event.getApplicantUserIds().isEmpty()) {
            return;
        }
        try {
            for (Long userId : event.getApplicantUserIds()) {
                userRepository.findById(userId).ifPresent(applicant ->
                        notificationService.send(
                                applicant,
                                NotificationType.JOB_EXPIRED,
                                NotificationPriority.MEDIUM,
                                "A Job You Applied to Has Been Removed",
                                "The job \"" + event.getJobTitle()
                                        + "\" has been removed by the recruiter. "
                                        + "Explore similar open positions.",
                                "/jobs",
                                event.getJobId(),
                                "JOB"
                        )
                );
            }
            log.info("JOB_EXPIRED notifications sent to {} applicant(s) for job [{}]",
                    event.getTotalApplicants(), event.getJobId());
        } catch (Exception ex) {
            log.error("Failed to send JOB_EXPIRED notifications for job [{}]: {}",
                    event.getJobId(), ex.getMessage(), ex);
        }
    }
}
