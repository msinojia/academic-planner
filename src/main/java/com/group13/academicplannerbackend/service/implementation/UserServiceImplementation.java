package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.model.VerificationCode;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VerificationCodeRepository;
import com.group13.academicplannerbackend.service.UserService;
import com.group13.academicplannerbackend.util.Constants;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImplementation implements UserService {
    private UserRepository userRepository;
    private UserMetaRepository userMetaRepository;
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    public UserServiceImplementation(
            UserRepository userRepository,
            UserMetaRepository userMetaRepository,
            VerificationCodeRepository verificationCodeRepository) {
        this.userRepository = userRepository;
        this.userMetaRepository = userMetaRepository;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    /**
     * @param user
     */
    @Override
    public void register(User user) {
        String password = user.getPasswordHash();
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPasswordHash(passwordHash);
        userRepository.save(user);

        UserMeta userMeta = new UserMeta();
        userMeta.setUser(user);
        userMetaRepository.save(userMeta);

        String code = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(Constants.VERIFICATION_CODE_EXPIRY_MINUTES);
        VerificationCode verificationCode = new VerificationCode(code, user.getEmail(), expiryTime);
        verificationCodeRepository.save(verificationCode);

        String verificationUrl = "https://example.com/verify?email=" + user.getEmail() + "&code=" + code;
        String subject = "Verify your email";
        String body = "Please click on this link to verify your email: " + verificationUrl;
//        emailService.sendEmail(user.getEmail(), subject, body);
        System.out.println(user.getEmail() + "\n" + subject + "\n" + body + "\n");
    }
}
