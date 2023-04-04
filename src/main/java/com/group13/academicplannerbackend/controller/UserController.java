package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.exception.UnAuthorizedUserException;
import com.group13.academicplannerbackend.exception.VerificationException;
import com.group13.academicplannerbackend.model.JwtResponse;
import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @CrossOrigin
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok("User registered successfully.");
    }

    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            String jwtToken = userService.loginProcess(user);
            JwtResponse jwtResponse = new JwtResponse(jwtToken);
            return ResponseEntity.ok(jwtResponse);
        } catch (UnAuthorizedUserException | VerificationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
