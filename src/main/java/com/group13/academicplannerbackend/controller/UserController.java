package com.group13.academicplannerbackend.controller;

import com.group13.academicplannerbackend.model.User;
import com.group13.academicplannerbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin
    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        userService.register(user);
        return "User registered successfully.";
    }

    @CrossOrigin
    @PostMapping("/login")
    public String login(@RequestBody User user) {

        return userService.loginProcess(user);

    }
}
