package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.config.SecurityConfig;
import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.dto.AuthResponse;
import com.apprenticareers.addressbook.dto.LoginRequest;
import com.apprenticareers.addressbook.dto.RegisterRequest;
import com.apprenticareers.addressbook.dto.UserResponse;
import com.apprenticareers.addressbook.exception.EmailAlreadyExistsException;
import com.apprenticareers.addressbook.exception.InvalidCredentialsException;
import com.apprenticareers.addressbook.security.CustomUserDetailsService;
import com.apprenticareers.addressbook.security.JwtService;
import com.apprenticareers.addressbook.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void register_returns201WithCreatedUser() throws Exception {
        RegisterRequest request = new RegisterRequest("new@example.com", "password1", "1 Elm St", "555-0300");
        UserResponse response = new UserResponse(1L, "new@example.com", "1 Elm St", "555-0300",
                User.Role.USER, LocalDateTime.now());
        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@example.com"));
    }

    @Test
    void register_returns400ForInvalidPayload() throws Exception {
        RegisterRequest invalid = new RegisterRequest("not-an-email", "", "", "");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns409WhenEmailAlreadyExists() throws Exception {
        RegisterRequest request = new RegisterRequest("dup@example.com", "password1", "1 Elm St", "555-0300");
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(new EmailAlreadyExistsException("dup@example.com"));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_returns200WithToken() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "password1");
        when(authService.login(any(LoginRequest.class)))
                .thenReturn(new AuthResponse("jwt-token", "user@example.com", "USER"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_returns401ForInvalidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
