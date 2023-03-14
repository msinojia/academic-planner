package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.exception.UnAuthorizedUserException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
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
    private VerificationService verificationService;

    @Autowired
    public UserServiceImplementation(
            UserRepository userRepository,
            UserMetaRepository userMetaRepository,
            VerificationService verificationService) {
        this.userRepository = userRepository;
        this.userMetaRepository = userMetaRepository;
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

    @Override
    public String loginProcess(User user) throws UnAuthorizedUserException {

        UserMeta userMeta = new UserMeta();
        User tempUser;
        tempUser = userRepository.findByEmail(user.getEmail());

        if (user.getEmail() != "") {

            Boolean password = BCrypt.checkpw(user.getPasswordHash(), tempUser.getPasswordHash());

            if (password) {

                userMeta = userMetaRepository.findByUser(tempUser);

                if (userMeta.isVerified() == true) {
                    return "true"; // return JWT token here
                } else {
                    throw new VerificationException("please varify your email");
                }

            } else {
                throw new UnAuthorizedUserException("Wrong username or password");
            }
        } else {
            throw new UnAuthorizedUserException("Wrong username or password");
        }
    }
}
