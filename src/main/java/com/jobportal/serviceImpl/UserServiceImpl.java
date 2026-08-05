package com.jobportal.serviceImpl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jobportal.domain.AccountType;
import com.jobportal.dto.request.RegisterRequest;
import com.jobportal.dto.response.UserResponse;
import com.jobportal.entity.Otp;
import com.jobportal.entity.Profile;
import com.jobportal.entity.Recruiter;
import com.jobportal.entity.User;
import com.jobportal.event.PasswordResetEvent;
import com.jobportal.event.UserRegisteredEvent;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.OtpRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.UserService;
import com.jobportal.utility.Utilities;

import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * User service implementation.
 *
 * <h3>Notifications (event-driven)</h3>
 * <ul>
 *   <li>{@link UserRegisteredEvent} — fired after registration.
 *       The listener sends a welcome {@code ACCOUNT} notification.</li>
 *   <li>{@link PasswordResetEvent} — fired after a successful OTP-verified password reset.
 *       The listener sends a {@code SECURITY} alert notification.</li>
 * </ul>
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository           userRepository;
    private final PasswordEncoder          passwordEncoder;
    private final JavaMailSender           mailSender;
    private final OtpRepository            otpRepository;
    private final ApplicationEventPublisher eventPublisher;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            OtpRepository otpRepository,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender      = mailSender;
        this.otpRepository   = otpRepository;
        this.eventPublisher  = eventPublisher;
    }

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) throws JobPortalException {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw JobPortalException.conflict(
                    "User already exists with email: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAccountType(request.getAccountType());

        if (request.getAccountType() == AccountType.APPLICANT) {
            Profile profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        } else if (request.getAccountType() == AccountType.EMPLOYER) {
            Recruiter recruiter = new Recruiter();
            recruiter.setUser(user);
            user.setRecruiter(recruiter);
        }

        User savedUser = userRepository.save(user);

        // ── Publish event: listener sends ACCOUNT welcome notification ────────
        eventPublisher.publishEvent(new UserRegisteredEvent(this, savedUser));

        return toUserResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) throws JobPortalException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found"));
        return toUserResponse(user);
    }

    @Override
    @Transactional
    public boolean sendOtp(String email) throws Exception {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found with email: " + email));

        String generatedOtp = Utilities.generateOtp();

        otpRepository.findByEmail(email).ifPresent(otpRepository::delete);

        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(generatedOtp);
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setVerified(false);
        otpRepository.save(otp);

        sendOtpEmail(user.getName(), email, generatedOtp);
        return true;
    }

    @Override
    @Transactional
    public boolean verifyOtp(String email, String otp) throws JobPortalException {
        Otp otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("OTP not found for email: " + email));

        if (LocalDateTime.now().isAfter(otpEntity.getExpiryTime())) {
            throw JobPortalException.badRequest("OTP has expired. Please request a new one.");
        }

        if (!otpEntity.getOtpCode().equals(otp)) {
            throw JobPortalException.badRequest("Invalid OTP. Please check and try again.");
        }

        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(String email, String newPassword) throws JobPortalException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.notFound("User not found"));

        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() -> JobPortalException.badRequest(
                        "OTP verification required before resetting password."));

        if (!otp.isVerified()) {
            throw JobPortalException.forbidden("OTP has not been verified. Please verify first.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpRepository.delete(otp);

        // ── Publish event: listener sends SECURITY alert notification ─────────
        eventPublisher.publishEvent(new PasswordResetEvent(this, user));

        return true;
    }

    // ── Private Helpers ─────────────────────────────────────────────────────

    private UserResponse toUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAccountType(user.getAccountType());
        response.setIsActive(user.getIsActive());
        response.setCreatedAt(user.getCreatedAt());

        if (user.getProfile() != null) {
            response.setProfileId(user.getProfile().getId());
        }
        if (user.getRecruiter() != null) {
            response.setRecruiterId(user.getRecruiter().getId());
        }
        return response;
    }

    private void sendOtpEmail(String name, String email, String otp) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
        message.setTo(email);
        message.setSubject("Velora — Password Reset OTP");

        String body = """
                <!DOCTYPE html>
                <html>
                <head><meta charset="UTF-8"></head>
                <body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;">
                <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f7fb;padding:40px 0;">
                    <tr><td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.08);">
                            <tr>
                                <td style="background:#2563EB;padding:28px;text-align:center;">
                                    <h1 style="margin:0;color:#ffffff;font-size:30px;">Velora</h1>
                                    <p style="margin:8px 0 0;color:#dbeafe;font-size:14px;">Smart Hiring • Better Careers</p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding:40px;">
                                    <h2 style="margin:0;color:#111827;">Password Reset Request</h2>
                                    <p style="margin-top:20px;color:#4b5563;font-size:16px;line-height:28px;">
                                        Hello <strong>%s</strong>,
                                    </p>
                                    <p style="color:#4b5563;font-size:16px;line-height:28px;">
                                        We received a request to reset your Velora account password.
                                        Use the verification code below to continue.
                                    </p>
                                    <div style="margin:35px 0;padding:22px;border:2px dashed #2563EB;border-radius:12px;background:#eff6ff;text-align:center;">
                                        <div style="font-size:13px;color:#6b7280;">YOUR VERIFICATION CODE</div>
                                        <div style="margin-top:10px;font-size:42px;letter-spacing:10px;font-weight:bold;color:#2563EB;">%s</div>
                                    </div>
                                    <p style="color:#ef4444;font-size:15px;">⏳ This OTP is valid for <strong>5 minutes</strong>.</p>
                                    <p style="color:#6b7280;font-size:15px;line-height:26px;">
                                        If you didn't request a password reset, you can safely ignore this email.
                                    </p>
                                    <hr style="margin:35px 0;border:none;border-top:1px solid #e5e7eb;">
                                    <p style="font-size:14px;color:#9ca3af;">This is an automated email. Please do not reply.</p>
                                </td>
                            </tr>
                            <tr>
                                <td style="background:#f9fafb;padding:25px;text-align:center;">
                                    <div style="font-size:18px;font-weight:bold;color:#111827;">Velora</div>
                                    <div style="margin-top:8px;color:#6b7280;font-size:14px;">Connecting Talent with Opportunities</div>
                                    <div style="margin-top:18px;color:#9ca3af;font-size:12px;">© 2026 Velora. All Rights Reserved.</div>
                                </td>
                            </tr>
                        </table>
                    </td></tr>
                </table>
                </body>
                </html>
                """.formatted(name, otp);

        message.setText(body, true);
        mailSender.send(mimeMessage);
    }
}