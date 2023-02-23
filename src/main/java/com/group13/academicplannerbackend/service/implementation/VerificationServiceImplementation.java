package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.exception.UserNotFoundException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.model.VerificationCode;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VerificationCodeRepository;
import com.group13.academicplannerbackend.service.EmailService;
import com.group13.academicplannerbackend.service.VerificationService;
import com.group13.academicplannerbackend.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VerificationServiceImplementation implements VerificationService {
    private VerificationCodeRepository verificationCodeRepository;
    private UserRepository userRepository;
    private EmailService emailService;
    @Value("${server.port}")
    private int serverPort;

    @Autowired
    public VerificationServiceImplementation(
            VerificationCodeRepository verificationCodeRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param code
     */
    @Override
    public void verify(String code, String email) throws VerificationException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new VerificationException("User not found");
        }

        UserMeta userMeta = user.getUserMeta();
        if(userMeta.isVerified()) {
            throw new VerificationException("User is already verified");
        }

        VerificationCode verificationCode = verificationCodeRepository.findByCodeAndEmail(code, email);
        if(verificationCode == null) {
            throw new VerificationException("Invalid verification code");
        }

        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new VerificationException("Verification code has expired");
        }

        userMeta.setVerified(true);
        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);
    }

    @Override
    public void sendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UserNotFoundException(email);
        }
        UserMeta userMeta = user.getUserMeta();
        if(userMeta != null && userMeta.isVerified()) {
            throw new VerificationException("User is already verified");
        }

        // Remove the existing code if expired
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        if(verificationCode != null && verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            verificationCodeRepository.delete(verificationCode);
            verificationCode = null;
        }

        // Create a new code if no code exists or if it has expired (and hence removed)
        if(verificationCode == null) {
            String code = UUID.randomUUID().toString();
            LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(Constants.VERIFICATION_CODE_EXPIRY_MINUTES);
            verificationCode = new VerificationCode(code, user.getEmail(), expiryTime);
            verificationCodeRepository.save(verificationCode);
        }

        // Generate verification URL and send email
        String verificationUrl = String.format(
                "0.0.0.0:%d/verify?email=%s&code=%s",
                serverPort,
                user.getEmail(),
                verificationCode.getCode()
        );
        String subject = "Verify your email";
        String body = "Please click on this link to verify your email: " + verificationUrl;
//        emailService.sendEmail(user.getEmail(), subject, body); //TODO: Send email instead of printing
        System.out.println(user.getEmail() + "\n" + subject + "\n" + body + "\n");
    }
}
