package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.repository.VerificationCodeRepository;
import com.group13.academicplannerbackend.service.UserService;
import com.group13.academicplannerbackend.service.VerificationService;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService {
    private UserRepository userRepository;
    private UserMetaRepository userMetaRepository;
    private VerificationCodeRepository verificationCodeRepository;
    private VerificationService verificationService;

    @Autowired
    public UserServiceImplementation(
            UserRepository userRepository,
            UserMetaRepository userMetaRepository,
            VerificationCodeRepository verificationCodeRepository,
            VerificationService verificationService) {
        this.userRepository = userRepository;
        this.userMetaRepository = userMetaRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.verificationService = verificationService;
    }

    /**
     * @param user
     */
    @Override
    @Transactional
    public void register(User user) {
        String password = user.getPasswordHash();
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPasswordHash(passwordHash);
        userRepository.save(user);

        UserMeta userMeta = new UserMeta();
        userMeta.setUser(user);
        userMetaRepository.save(userMeta);

        verificationService.sendVerificationEmail(user.getEmail());
    }
}
