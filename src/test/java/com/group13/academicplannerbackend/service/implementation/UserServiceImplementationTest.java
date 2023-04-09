package com.group13.academicplannerbackend.service.implementation;

import com.group13.academicplannerbackend.exception.UnAuthorizedUserException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.JwtResponse;
import com.group13.academicplannerbackend.model.ProfileStatus;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.model.UserMeta;
import com.group13.academicplannerbackend.repository.UserMetaRepository;
import com.group13.academicplannerbackend.repository.UserRepository;
import com.group13.academicplannerbackend.service.UserService;
import com.group13.academicplannerbackend.service.VerificationService;
import com.group13.academicplannerbackend.util.JWTUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplementationTest {

    @InjectMocks
    private UserServiceImplementation userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMetaRepository userMetaRepository;

    @Mock
    private VerificationService verificationService;

    @Mock
    private JWTUtil jwtUtil;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRegister_Success() {
        // Arrange
        User user = new User();
        user.setEmail("pankti@gmail.com");
        user.setPasswordHash("pankti2510");

        UserMeta userMeta = new UserMeta();
        userMeta.setProfileStatus(ProfileStatus.UNSET);

        // Act
        userService.register(user);

        // Assert
        Mockito.verify(userRepository).save(Mockito.any(User.class));
        Mockito.verify(userMetaRepository).save(Mockito.any(UserMeta.class));
        Mockito.verify(verificationService).sendVerificationEmail(anyString());
    }

    @Test
    public void testRegisterUserSuccess() {
        // Create a User object with necessary data
        User user = new User();
        user.setFirstName("Pankti");
        user.setLastName("Patel");
        user.setPasswordHash("pankti25");
        user.setEmail("pankti@gmail.com");

        // Mock the UserRepository and UserMetaRepository
        UserRepository userRepositoryMock = mock(UserRepository.class);
        UserMetaRepository userMetaRepositoryMock = mock(UserMetaRepository.class);

        // Mock the VerificationService
        VerificationService verificationServiceMock = mock(VerificationService.class);
        doNothing().when(verificationServiceMock).sendVerificationEmail(anyString());

        // Create an instance of UserServiceImplementation with the mocked repositories and verification service
        UserServiceImplementation userService = new UserServiceImplementation(userRepositoryMock, userMetaRepositoryMock, verificationServiceMock, null);

        // Call the register method
        userService.register(user);

        // Verify that the user is saved in UserRepository
        verify(userRepositoryMock).save(user);

        // Verify that a UserMeta object is saved in UserMetaRepository with the correct profile status and user
        ArgumentCaptor<UserMeta> userMetaCaptor = ArgumentCaptor.forClass(UserMeta.class);
        verify(userMetaRepositoryMock).save(userMetaCaptor.capture());
        assertEquals(ProfileStatus.UNSET, userMetaCaptor.getValue().getProfileStatus());
        assertEquals(user, userMetaCaptor.getValue().getUser());

        // Verify that the sendVerificationEmail method of VerificationService is called with the correct email
        verify(verificationServiceMock).sendVerificationEmail(user.getEmail());
    }

    
}