package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Implementation to follow in Iteration 1 tasks
    // - createUser(email, role, address, phone)
    // - removeUser(userId)
    // - resetUserPassword(userId) -> new random password
}
