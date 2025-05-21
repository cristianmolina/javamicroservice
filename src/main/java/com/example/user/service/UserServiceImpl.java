package com.example.user.service;

import com.example.user.configuration.JwtProperties;
import com.example.user.constantes.Constants;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    public UserServiceImpl(UserRepository userRepository, JwtProperties jwtProperties) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(4);
        this.jwtProperties = jwtProperties;
    }

    @Override
    public User signUp(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException(Constants.USER_ALREADY_EXISTS);
                });

        String idSession = UUID.randomUUID().toString();
        String token = generateToken(user.getEmail(), idSession);

        user.setId(UUID.randomUUID());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreated(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setSessionId(passwordEncoder.encode(idSession));
        user.setActive(true);

        User saved = userRepository.save(user);
        saved.setToken(token);
        return saved;
    }

    @Override
    public User login(String token) {

        try {
            token = token.replace("Bearer ", "");
            String email = getEmailFromToken(token);

            String sessionId = getSessionIdFromToken(token);

            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (passwordEncoder.matches(sessionId, user.getSessionId())) {
                    String idSessionNuevo = UUID.randomUUID().toString();
                    String tokenNuevo = generateToken(user.getEmail(), idSessionNuevo);
                    user.setLastLogin(LocalDateTime.now());
                    user.setSessionId(passwordEncoder.encode(idSessionNuevo));
                    User saved = userRepository.save(user);

                    saved.setToken(tokenNuevo);
                    return saved;
                } else {
                    throw new IllegalArgumentException(Constants.INVALID_TOKEN);
                }
            } else {
                throw new IllegalArgumentException(Constants.USER_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,Constants.INVALID_CREDENTIALS, e);
        }

    }

    private String getSessionIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(jwtProperties.getSecret()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    private String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(jwtProperties.getSecret()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String generateToken(String email, String sessionId) {

        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtProperties.getSecret()));

        return Jwts.builder()
                .setId(sessionId)
                .setSubject(email)
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}