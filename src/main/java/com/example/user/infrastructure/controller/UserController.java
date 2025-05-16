package com.example.user.infrastructure.controller;

import com.example.user.application.usecase.UserService;
import com.example.user.domain.model.User;
import com.example.user.infrastructure.controller.dto.UserMapper;
import com.example.user.infrastructure.controller.dto.UserRequestDto;
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
        // Convierte el DTO a la entidad User
        User user = userMapper.mapToEntity(userRequestDto);
        // Guarda el usuario y retorna la información.
        return ResponseEntity.ok(userMapper.mapToEntity(userService.signUp(user)));
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestHeader("Authorization") String token) {
        // Consultar el usuario por el token
        User user = userService.login(token);
        // Retornar la información del usuario
        return ResponseEntity.ok(userMapper.mapToEntity(user));
    }
}