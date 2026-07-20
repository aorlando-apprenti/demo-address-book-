package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupAdminSeederService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Implementation to follow in Iteration 1 tasks
        // - Check if any ADMIN user exists
        // - If not, create initial admin with random password
        // - Log password for operator to retrieve
    }
}
