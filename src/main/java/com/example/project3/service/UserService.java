package com.example.project3.service;

import com.example.project3.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {
    void register(User user);
    String confirm(String token);
    Optional<User> findUserByEmail(String email);
    Integer updatePassword(String password, String email);
}
