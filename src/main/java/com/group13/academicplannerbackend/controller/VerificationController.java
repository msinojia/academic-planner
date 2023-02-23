package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationController {
    private VerificationService verificationService;

    @Autowired
    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping("/verify")
    public void verify(@RequestParam("code") String code, @RequestParam("email") String email) {
        verificationService.verify(code, email);
    }
}
