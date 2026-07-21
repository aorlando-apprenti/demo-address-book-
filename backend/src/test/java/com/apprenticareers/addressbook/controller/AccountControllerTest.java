package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.config.SecurityConfig;
import com.apprenticareers.addressbook.dto.ChangePasswordRequest;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(SecurityConfig.class)
class AccountControllerTest {

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
    @WithMockUser(username = "user@example.com")
    void changePassword_returns200ForAuthenticatedUser() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass1", "newPass1");
        doNothing().when(authService).changePassword(anyString(), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(authService).changePassword("user@example.com", request);
    }

    @Test
    void changePassword_returns401WhenUnauthenticated() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass1", "newPass1");

        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void changePassword_returns401WhenOldPasswordIncorrect() throws Exception {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongOld", "newPass1");
        doThrow(new InvalidCredentialsException("Current password is incorrect"))
                .when(authService).changePassword(anyString(), any(ChangePasswordRequest.class));

        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void changePassword_returns400ForInvalidPayload() throws Exception {
        ChangePasswordRequest invalid = new ChangePasswordRequest("", "");

        mockMvc.perform(put("/account/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
