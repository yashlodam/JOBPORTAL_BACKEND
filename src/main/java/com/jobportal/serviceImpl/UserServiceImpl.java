package com.jobportal.serviceImpl;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.dto.LoginDTO;
import com.jobportal.dto.ProfileDTO;
import com.jobportal.dto.UserDTO;
import com.jobportal.entity.Otp;
import com.jobportal.entity.Profile;
import com.jobportal.entity.User;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repository.OtpRepository;
import com.jobportal.repository.UserRepository;
import com.jobportal.service.ProfileService;
import com.jobportal.service.UserService;
import com.jobportal.utility.Utilities;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private OtpRepository otpRepository;

   
    
    @Override
    public UserDTO registerUser(UserDTO userDTO) throws JobPortalException {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new JobPortalException("User already exists with email: " + userDTO.getEmail());
        }

        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setAccountType(userDTO.getAccountType());

        Profile profile = new Profile();
        profile.setEmail(userDTO.getEmail());

        user.setProfile(profile);
        profile.setUser(user);

        User savedUser = userRepository.save(user);

        return mapper.map(savedUser, UserDTO.class);
    }
    
    @Override
    public UserDTO loginUser(LoginDTO loginDTO) throws JobPortalException {

        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new JobPortalException("Email does not exist"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new JobPortalException("Invalid Credentials");
        }

        return mapper.map(user,UserDTO.class);
    }

    @Override
    public Boolean sendOtp(String email) throws Exception {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));

        // Generate OTP
        String generatedOtp = Utilities.generateOtp();

        // Remove old OTP if it exists
        otpRepository.findByEmail(email)
                .ifPresent(otpRepository::delete);

        // Save new OTP
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(generatedOtp);
        otp.setCreationTime(LocalDateTime.now());
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        otp.setVerified(false);

        otpRepository.save(otp);

        // Create email
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

        message.setTo(email);
        message.setSubject("Velora - Password Reset OTP");

        String body = """
        		<!DOCTYPE html>
        		<html>
        		<head>
        		    <meta charset="UTF-8">
        		</head>
        		<body style="margin:0;padding:0;background:#f4f7fb;font-family:Arial,Helvetica,sans-serif;">

        		<table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f7fb;padding:40px 0;">
        		    <tr>
        		        <td align="center">

        		            <table width="600" cellpadding="0" cellspacing="0"
        		                   style="background:#ffffff;border-radius:16px;overflow:hidden;
        		                   box-shadow:0 8px 30px rgba(0,0,0,0.08);">

        		                <!-- Header -->
        		                <tr>
        		                    <td style="background:#2563EB;padding:28px;text-align:center;">
        		                        <h1 style="margin:0;color:#ffffff;font-size:30px;">
        		                            Velora
        		                        </h1>
        		                        <p style="margin:8px 0 0;color:#dbeafe;font-size:14px;">
        		                            Smart Hiring • Better Careers
        		                        </p>
        		                    </td>
        		                </tr>

        		                <!-- Body -->
        		                <tr>
        		                    <td style="padding:40px;">

        		                        <h2 style="margin:0;color:#111827;">
        		                            Password Reset Request
        		                        </h2>

        		                        <p style="margin-top:20px;color:#4b5563;font-size:16px;line-height:28px;">
        		                            Hello <strong>%s</strong>,
        		                        </p>

        		                        <p style="color:#4b5563;font-size:16px;line-height:28px;">
        		                            We received a request to reset your Velora account password.
        		                            Use the verification code below to continue.
        		                        </p>

        		                        <div style="
        		                            margin:35px 0;
        		                            padding:22px;
        		                            border:2px dashed #2563EB;
        		                            border-radius:12px;
        		                            background:#eff6ff;
        		                            text-align:center;">

        		                            <div style="font-size:13px;color:#6b7280;">
        		                                YOUR VERIFICATION CODE
        		                            </div>

        		                            <div style="
        		                                margin-top:10px;
        		                                font-size:42px;
        		                                letter-spacing:10px;
        		                                font-weight:bold;
        		                                color:#2563EB;">
        		                                %s
        		                            </div>

        		                        </div>

        		                        <p style="color:#ef4444;font-size:15px;">
        		                            ⏳ This OTP is valid for <strong>5 minutes</strong>.
        		                        </p>

        		                        <p style="color:#6b7280;font-size:15px;line-height:26px;">
        		                            If you didn't request a password reset,
        		                            you can safely ignore this email.
        		                            Your password will remain unchanged.
        		                        </p>

        		                        <hr style="margin:35px 0;border:none;border-top:1px solid #e5e7eb;">

        		                        <p style="font-size:14px;color:#9ca3af;">
        		                            This is an automated email. Please do not reply.
        		                        </p>

        		                    </td>
        		                </tr>

        		                <!-- Footer -->
        		                <tr>
        		                    <td style="background:#f9fafb;padding:25px;text-align:center;">

        		                        <div style="font-size:18px;font-weight:bold;color:#111827;">
        		                            Velora
        		                        </div>

        		                        <div style="margin-top:8px;color:#6b7280;font-size:14px;">
        		                            Connecting Talent with Opportunities
        		                        </div>

        		                        <div style="margin-top:18px;color:#9ca3af;font-size:12px;">
        		                            © 2026 Velora. All Rights Reserved.
        		                        </div>

        		                    </td>
        		                </tr>

        		            </table>

        		        </td>
        		    </tr>
        		</table>

        		</body>
        		</html>
        		""".formatted(user.getName(), generatedOtp);

        message.setText(body, true);

        mailSender.send(mimeMessage);

        return true;
    }

    @Override
    public Boolean verifyOtp(String email, String otp) {

        Otp otpEntity = otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        // Check if OTP is expired
        if (LocalDateTime.now().isAfter(otpEntity.getExpiryTime())) {
            throw new RuntimeException("OTP has expired");
        }

        // Verify OTP
        if (!otpEntity.getOtpCode().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Mark OTP as verified
        otpEntity.setVerified(true);
        otpRepository.save(otpEntity);

        return true;
    }

    @Override
    public Boolean resetPassword(String email, String newPassword) throws JobPortalException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("USER_NOT_FOUND"));

        Otp otp = otpRepository.findByEmail(email)
                .orElseThrow(() -> new JobPortalException("OTP_NOT_FOUND"));

        // Check whether OTP was verified
        if (!otp.isVerified()) {
            throw new JobPortalException("OTP_NOT_VERIFIED");
        }

        // Encode new password
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // Delete OTP after successful reset
        otpRepository.delete(otp);

        return true;
    }

}