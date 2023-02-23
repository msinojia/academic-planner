package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.model.VerificationCode;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VerificationCodeRepository;
import com.group13.academicplannerbackend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VerificationServiceImplementation implements VerificationService {
    private VerificationCodeRepository verificationCodeRepository;
    private UserRepository userRepository;

    @Autowired
    public VerificationServiceImplementation(
            VerificationCodeRepository verificationCodeRepository,
            UserRepository userRepository) {
        this.verificationCodeRepository = verificationCodeRepository;
        this.userRepository = userRepository;
    }

    /**
     * @param code
     */
    @Override
    public void verify(String code, String email) throws VerificationException {
        VerificationCode verificationCode = verificationCodeRepository.findByCodeAndEmail(code, email);
        if(verificationCode == null) {
            throw new VerificationException("Invalid verification code");
        }

        if (verificationCode.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new VerificationException("Verification code has expired");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new VerificationException("User not found");
        }

        UserMeta userMeta = user.getUserMeta();
        if(userMeta.isVerified()) {
            throw new VerificationException("User is already verified");
        }
        userMeta.setVerified(true);
        userRepository.save(user);
        verificationCodeRepository.delete(verificationCode);
    }
}
