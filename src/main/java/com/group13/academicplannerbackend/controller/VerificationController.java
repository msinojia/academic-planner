package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {

    @Autowired
    private VerificationService verificationService;

    @GetMapping("/verify")
public ResponseEntity<String> verify(@RequestParam("code") String code, @RequestParam("email") String email) {
    try {
        verificationService.verify(code, email);
        String successMessage = "Email verification successful";
        String loginLink = "/auth/login";
        String responseBody = generateResponse(successMessage, loginLink);
        return ResponseEntity.ok(responseBody);
    } catch (VerificationException e) {
        boolean alreadyVerified = "User is already verified".equals(e.getMessage());
        String errorMessage = alreadyVerified ? "User is already verified." : "Email verification failed.";
        String extraMessage = alreadyVerified ? generateLoginLink() : generateResendLink(email);
        String responseBody = generateResponse(errorMessage, extraMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}

private String generateResponse(String message, String extraContent) {
    return String.format("<!DOCTYPE html>%n" +
            "<html>%n" +
            "<head><title>Email Verification</title></head>%n" +
            "<body><h1>%s</h1><p>%s</p></body>%n" +
            "</html>", message, extraContent);
}

private String generateLoginLink() {
    return "<p>Please <a href='/auth/login'>log in</a> to continue.</p>";
}

private String generateResendLink(String email) {
    return String.format("<p>Please <a href='/resend-verification?email=%s'>resend the verification email</a> and try again.</p>", email);
}

    @GetMapping("/resend-verification")
    public void resendVerificationEmail(@RequestParam("email") String email) {
        verificationService.sendVerificationEmail(email);
    }
}
