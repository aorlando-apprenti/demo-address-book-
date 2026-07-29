package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.config.SecurityConfig;
import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.dto.CreateUserRequest;
import com.apprenticareers.addressbook.dto.CreateUserResponse;
import com.apprenticareers.addressbook.dto.ResetPasswordResponse;
import com.apprenticareers.addressbook.dto.UserResponse;
import com.apprenticareers.addressbook.exception.UserNotFoundException;
import com.apprenticareers.addressbook.security.CustomUserDetailsService;
import com.apprenticareers.addressbook.security.JwtService;
import com.apprenticareers.addressbook.service.AdminUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@Import(SecurityConfig.class)
class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminUserService adminUserService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private CreateUserRequest validRequest() {
        return new CreateUserRequest("new@example.com", "1 Elm St", null, "Springfield", "IL", "62701", "555-0300");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_returns201ForAdmin() throws Exception {
        UserResponse userResponse = new UserResponse(10L, "new@example.com", "1 Elm St", null, "Springfield", "IL", "62701", "555-0300",
                User.Role.USER, LocalDateTime.now());
        when(adminUserService.createUser(any(CreateUserRequest.class)))
                .thenReturn(new CreateUserResponse(userResponse, "TempPass123"));

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.email").value("new@example.com"))
                .andExpect(jsonPath("$.temporaryPassword").value("TempPass123"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser_returns403ForNonAdmin() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    void createUser_returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_returns400ForInvalidPayload() throws Exception {
        CreateUserRequest invalid = new CreateUserRequest("not-an-email", "", null, "", "", "", "");

        mockMvc.perform(post("/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeUser_returns200ForAdmin() throws Exception {
        mockMvc.perform(delete("/admin/users/5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void removeUser_returns404WhenUserNotFound() throws Exception {
        org.mockito.Mockito.doThrow(new UserNotFoundException(999L))
                .when(adminUserService).removeUser(999L);

        mockMvc.perform(delete("/admin/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void removeUser_returns403ForNonAdmin() throws Exception {
        mockMvc.perform(delete("/admin/users/5"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void resetPassword_returns200ForAdmin() throws Exception {
        when(adminUserService.resetUserPassword(anyLong()))
                .thenReturn(new ResetPasswordResponse(5L, "target@example.com", "NewPass123"));

        mockMvc.perform(post("/admin/users/5/reset-password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newPassword").value("NewPass123"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void resetPassword_returns403ForNonAdmin() throws Exception {
        mockMvc.perform(post("/admin/users/5/reset-password"))
                .andExpect(status().isForbidden());
    }
}
