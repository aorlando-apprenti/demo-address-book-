package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.dto.CreateUserRequest;
import com.apprenticareers.addressbook.dto.CreateUserResponse;
import com.apprenticareers.addressbook.dto.ResetPasswordResponse;
import com.apprenticareers.addressbook.exception.EmailAlreadyExistsException;
import com.apprenticareers.addressbook.exception.UserNotFoundException;
import com.apprenticareers.addressbook.repository.ContactRepository;
import com.apprenticareers.addressbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    private User targetUser;

    @BeforeEach
    void setUp() {
        targetUser = new User();
        targetUser.setId(5L);
        targetUser.setEmail("target@example.com");
        targetUser.setPasswordHash("old-hash");
        targetUser.setRole(User.Role.USER);
    }

    @Test
    void createUser_persistsUserWithGeneratedPasswordAndUserRole() {
        CreateUserRequest request = new CreateUserRequest("new@example.com", "1 Elm St", "555-0300");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-temp-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        CreateUserResponse response = adminUserService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getRole()).isEqualTo(User.Role.USER);
        assertThat(saved.getPasswordHash()).isEqualTo("hashed-temp-password");
        assertThat(response.getUser().getEmail()).isEqualTo("new@example.com");
        assertThat(response.getTemporaryPassword()).isNotBlank();
    }

    @Test
    void createUser_throwsWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest("target@example.com", "1 Elm St", "555-0300");
        when(userRepository.existsByEmail("target@example.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> adminUserService.createUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void removeUser_deletesExistingUser() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(targetUser));

        adminUserService.removeUser(5L);

        verify(userRepository).delete(targetUser);
    }

    @Test
    void removeUser_cascadesToDeleteOwnedContactsBeforeDeletingUser() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(targetUser));

        adminUserService.removeUser(5L);

        InOrder inOrder = inOrder(contactRepository, userRepository);
        inOrder.verify(contactRepository).deleteByOwnerUserId(5L);
        inOrder.verify(userRepository).delete(targetUser);
    }

    @Test
    void removeUser_throwsWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminUserService.removeUser(999L));
        verify(userRepository, never()).delete(any());
        verify(contactRepository, never()).deleteByOwnerUserId(any());
    }

    @Test
    void resetUserPassword_generatesAndPersistsNewHashedPassword() {
        when(userRepository.findById(5L)).thenReturn(Optional.of(targetUser));
        when(passwordEncoder.encode(anyString())).thenReturn("new-hashed-password");

        ResetPasswordResponse response = adminUserService.resetUserPassword(5L);

        assertThat(targetUser.getPasswordHash()).isEqualTo("new-hashed-password");
        assertThat(response.getUserId()).isEqualTo(5L);
        assertThat(response.getEmail()).isEqualTo("target@example.com");
        assertThat(response.getNewPassword()).isNotBlank();
        verify(userRepository).save(targetUser);
    }

    @Test
    void resetUserPassword_throwsWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminUserService.resetUserPassword(999L));
    }
}
