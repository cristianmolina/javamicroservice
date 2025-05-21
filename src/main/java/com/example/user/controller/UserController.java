package com.example.user.controller;

import com.example.user.service.UserService;
import com.example.user.model.User;
import com.example.user.converter.UserMapper;
import com.example.user.dto.UserRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    private UserMapper userMapper;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserRequestDto userRequestDto) {
        User user = userMapper.mapToEntity(userRequestDto);
        return ResponseEntity.ok(userMapper.mapToEntity(userService.signUp(user)));
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String token) {
        User user = userService.login(token);
        return ResponseEntity.ok(userMapper.mapToEntity(user));
    }
}