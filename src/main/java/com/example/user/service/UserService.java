package com.example.user.service;

import com.example.user.model.User;

public interface UserService {
    User signUp(User user);
    User login(String token);
}
