package com.example.user.application.usecase;

import com.example.user.domain.model.User;
import com.example.user.infrastructure.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${jwt.secret}")
    private String secretKey;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(4);
    }

    @Override
    public User signUp(User user) {
        userRepository.findByEmail(user.getEmail())
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("User already exists");
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
            // normalizar el token
            token = token.replace("Bearer ", "");
            // obtener el email del token
            String email = getEmailFromToken(token);
            // obtener el sessionId del token
            String sessionId = getSessionIdFromToken(token);

            // buscar el usuario por email
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                // verificar si el token es válido
                if (passwordEncoder.matches(sessionId, user.getSessionId())) {
                    // actualizar el último inicio de sesión
                    String idSessionNuevo = UUID.randomUUID().toString();
                    String tokenNuevo = generateToken(user.getEmail(), idSessionNuevo);
                    user.setLastLogin(LocalDateTime.now());
                    user.setSessionId(passwordEncoder.encode(idSessionNuevo));
                    User saved = userRepository.save(user);

                    // asociar el nuevo token a la respuesta
                    saved.setToken(tokenNuevo);
                    return saved;
                } else {
                    throw new IllegalArgumentException("Invalid token");
                }
            } else {
                throw new IllegalArgumentException("User not found");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token", e);
        }

    }

    private String getSessionIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getId();
    }

    private String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Base64.getDecoder().decode(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private String generateToken(String email, String sessionId) {

        Key key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));

        return Jwts.builder()
                .setId(sessionId)
                .setSubject(email)
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
}