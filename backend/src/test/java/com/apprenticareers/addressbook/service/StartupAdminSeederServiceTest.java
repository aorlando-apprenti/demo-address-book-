package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StartupAdminSeederServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private StartupAdminSeederService seederService;

    @BeforeEach
    void setUp() {
        seederService = new StartupAdminSeederService(userRepository, passwordEncoder);
        ReflectionTestUtils.setField(seederService, "seedAdminEmail", "admin@addressbook.local");
        ReflectionTestUtils.setField(seederService, "seedAdminAddressLine1", "HQ");
        ReflectionTestUtils.setField(seederService, "seedAdminAddressLine2", "");
        ReflectionTestUtils.setField(seederService, "seedAdminCity", "Headquarters");
        ReflectionTestUtils.setField(seederService, "seedAdminState", "DC");
        ReflectionTestUtils.setField(seederService, "seedAdminZipCode", "00000");
        ReflectionTestUtils.setField(seederService, "seedAdminTelephone", "000-000-0000");
    }

    @Test
    void run_createsInitialAdminWhenNoneExists() throws Exception {
        when(userRepository.existsByRole(User.Role.ADMIN)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-admin-password");

        seederService.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedAdmin = captor.getValue();

        assertThat(savedAdmin.getEmail()).isEqualTo("admin@addressbook.local");
        assertThat(savedAdmin.getPasswordHash()).isEqualTo("hashed-admin-password");
        assertThat(savedAdmin.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    void run_skipsSeedingWhenAdminAlreadyExists() throws Exception {
        when(userRepository.existsByRole(User.Role.ADMIN)).thenReturn(true);

        seederService.run();

        verify(userRepository, never()).save(any());
    }
}
