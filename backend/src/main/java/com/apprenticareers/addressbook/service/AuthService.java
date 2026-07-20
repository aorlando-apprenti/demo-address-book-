package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Implementation to follow in Iteration 1 tasks
    // - register(email, password, address, phone)
    // - login(email, password) -> JWT token
    // - changePassword(userId, oldPassword, newPassword)
}
