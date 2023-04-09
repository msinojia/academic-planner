package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.exception.UserNotFoundException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.service.UserService;
import com.group13.academicplannerbackend.service.VerificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class VerificationControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private VerificationController verificationController;

    @Mock
    private VerificationService verificationService;

    @Mock
    private UserService userService; // You need this because JWTRequestFilter depends on UserDetailsService

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(verificationController).build();
    }

    @Test
    public void testSuccessfulVerification() throws Exception {
        mockMvc.perform(get("/verify?code=valid_code&email=test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email verification successful")));
    }

    @Test
    public void testNonExistingUserVerification() throws Exception {
        doThrow(new VerificationException("User not found")).when(verificationService).verify("valid_code", "non_existing@example.com");

        mockMvc.perform(get("/verify?code=valid_code&email=non_existing@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User not found")));
    }

    @Test
    public void testAlreadyVerifiedUser() throws Exception {
        doThrow(new VerificationException("User is already verified")).when(verificationService).verify("valid_code", "already_verified@example.com");

        mockMvc.perform(get("/verify?code=valid_code&email=already_verified@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("User is already verified")));
    }

    @Test
    public void testInvalidVerificationCode() throws Exception {
        doThrow(new VerificationException("Invalid verification code")).when(verificationService).verify("invalid_code", "test@example.com");

        mockMvc.perform(get("/verify?code=invalid_code&email=test@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid verification code")));
    }

    @Test
    public void testExpiredVerificationCode() throws Exception {
        doThrow(new VerificationException("Verification code has expired")).when(verificationService).verify("expired_code", "test@example.com");

        mockMvc.perform(get("/verify?code=expired_code&email=test@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Verification code has expired")));
    }

    @Test
    public void testResendVerificationEmailNonExistingUser() {
        String email = "non_existing@example.com";
        Mockito.doThrow(new UserNotFoundException(email)).when(verificationService).sendVerificationEmail(email);

        try {
            verificationController.resendVerificationEmail(email);
        } catch (UserNotFoundException e) {
            // Expected exception
        }
    }

    @Test
    public void testResendVerificationEmailSuccess() {
        String email = "existing@example.com";
        Mockito.doNothing().when(verificationService).sendVerificationEmail(email);

        verificationController.resendVerificationEmail(email);

        Mockito.verify(verificationService, Mockito.times(1)).sendVerificationEmail(email);
    }
}
