package com.example.user.application.usecase;

import com.example.user.domain.model.User;

public interface UserService {
    User signUp(User user);
    User login(String token);
}
