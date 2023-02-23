package com.group13.academicplannerbackend.service;

import com.group13.academicplannerbackend.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void register(User user);
}
