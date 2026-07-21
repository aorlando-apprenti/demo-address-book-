package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.dto.AuthResponse;
import com.apprenticareers.addressbook.dto.ChangePasswordRequest;
import com.apprenticareers.addressbook.dto.LoginRequest;
import com.apprenticareers.addressbook.dto.RegisterRequest;
import com.apprenticareers.addressbook.dto.UserResponse;
import com.apprenticareers.addressbook.exception.EmailAlreadyExistsException;
import com.apprenticareers.addressbook.exception.InvalidCredentialsException;
import com.apprenticareers.addressbook.exception.UserNotFoundException;
import com.apprenticareers.addressbook.repository.UserRepository;
import com.apprenticareers.addressbook.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("user@example.com");
        existingUser.setPasswordHash("hashed-old-password");
        existingUser.setAddress("123 Main St");
        existingUser.setTelephoneNumber("555-0100");
        existingUser.setRole(User.Role.USER);
    }

    @Test
    void register_persistsNewUserWithHashedPasswordAndUserRole() {
        RegisterRequest request = new RegisterRequest("new@example.com", "plainPassword", "456 Oak St", "555-0200");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });

        UserResponse response = authService.register(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(saved.getRole()).isEqualTo(User.Role.USER);
        assertThat(response.getEmail()).isEqualTo("new@example.com");
        assertThat(response.getId()).isEqualTo(2L);
    }

    @Test
    void register_throwsWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest("user@example.com", "plainPassword", "123 Main St", "555-0100");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsTokenForValidCredentials() {
        LoginRequest request = new LoginRequest("user@example.com", "correctPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("correctPassword", "hashed-old-password")).thenReturn(true);
        when(jwtService.generateToken(existingUser)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getEmail()).isEqualTo("user@example.com");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    void login_throwsForUnknownEmail() {
        LoginRequest request = new LoginRequest("missing@example.com", "anyPassword");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_throwsForWrongPassword() {
        LoginRequest request = new LoginRequest("user@example.com", "wrongPassword");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongPassword", "hashed-old-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void changePassword_updatesHashWhenOldPasswordMatches() {
        ChangePasswordRequest request = new ChangePasswordRequest("correctOldPassword", "newPassword123");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("correctOldPassword", "hashed-old-password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashed-new-password");

        authService.changePassword("user@example.com", request);

        assertThat(existingUser.getPasswordHash()).isEqualTo("hashed-new-password");
        verify(userRepository).save(existingUser);
    }

    @Test
    void changePassword_throwsWhenOldPasswordIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest("wrongOldPassword", "newPassword123");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("wrongOldPassword", "hashed-old-password")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.changePassword("user@example.com", request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_throwsWhenUserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword123");
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> authService.changePassword("missing@example.com", request));
    }
}
