package com.example.user.infrastructure.controller;

import com.example.user.infrastructure.controller.dto.PhoneDto;
import com.example.user.infrastructure.controller.dto.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSignUp_Success() throws Exception {
        UserRequestDto user = new UserRequestDto();
        user.setName("John Doe");
        user.setEmail("test1@example.com");
        user.setPassword("a2asfGfdfdf4");
        user.setPhones(Arrays.asList(
                new PhoneDto("313225584","7","25"),
                new PhoneDto("313655559","8","26")));

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("test1@example.com"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.lastLogin").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones[0].number").value("313225584"))
                .andExpect(jsonPath("$.phones[0].citycode").value("7"))
                .andExpect(jsonPath("$.phones[0].contrycode").value("25"))
                .andExpect(jsonPath("$.phones[1].number").value("313655559"))
                .andExpect(jsonPath("$.phones[1].citycode").value("8"))
                .andExpect(jsonPath("$.phones[1].contrycode").value("26"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testSignUp_InvalidPassword() throws Exception {
        UserRequestDto user = new UserRequestDto();
        user.setEmail("test2@example.com");
        user.setPassword("invalidpass"); // Contraseña inválida

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation error: {password=Password must have one uppercase letter, two digits, and be 8-12 characters long}"));
    }

    @Test
    void testSignUp_InvalidEmail() throws Exception {
        UserRequestDto user = new UserRequestDto();
        user.setEmail("invalid-email");
        user.setPassword("a2asfGfdfdf4");

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation error: {email=Invalid email format}"));
    }

    @Test
    void testSignUp_UserAlreadyExists() throws Exception {
        UserRequestDto user = new UserRequestDto();
        user.setEmail("test3@example.com");
        user.setPassword("a2asfGfdfdf4");

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("User already exists"));
    }

    @Test
    void testSignUp_MissingOptionalFields() throws Exception {
        UserRequestDto user = new UserRequestDto();
        user.setEmail("test4@example.com");
        user.setPassword("a2asfGfdfdf4");

        mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isEmpty())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.email").value("test4@example.com"))
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.lastLogin").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.phones").isEmpty())
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    void testLogin_Success() throws Exception {
        // Step 1: Create a user via /sign-up
        UserRequestDto user = new UserRequestDto();
        user.setName("Julio Gonzalez");
        user.setEmail("julio@testssw.cl");
        user.setPassword("a2asfGfdfdf4");
        user.setPhones(Arrays.asList(
                new PhoneDto("87650009", "7", "25")
        ));

        String signUpResponse = mockMvc.perform(post("/users/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the token from the /sign-up response
        String token = objectMapper.readTree(signUpResponse).get("token").asText();

        // Step 2: Use the token to log in via /login
        mockMvc.perform(get("/users/login")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.lastLogin").isNotEmpty())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.name").value("Julio Gonzalez"))
                .andExpect(jsonPath("$.email").value("julio@testssw.cl"))
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andExpect(jsonPath("$.phones").isArray())
                .andExpect(jsonPath("$.phones[0].number").value("87650009"))
                .andExpect(jsonPath("$.phones[0].citycode").value("7"))
                .andExpect(jsonPath("$.phones[0].contrycode").value("25"));
    }

    @Test
    void testLogin_InvalidToken() throws Exception {
        // Use an invalid token
        String invalidToken = "Bearer invalid-token";

        mockMvc.perform(get("/users/login")
                        .header("Authorization", invalidToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Invalid or expired token"));
    }



}