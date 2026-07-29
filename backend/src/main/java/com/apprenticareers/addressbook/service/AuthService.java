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
    private final JwtService jwtService;

    /**
     * FR-01: register a new self-service account. Always provisioned with role USER.
     */
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setAddressLine1(request.getAddressLine1());
        user.setAddressLine2(request.getAddressLine2());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipCode(request.getZipCode());
        user.setTelephoneNumber(request.getTelephoneNumber());
        user.setRole(User.Role.USER);

        User saved = userRepository.save(user);
        return UserResponse.from(saved);
    }

    /**
     * Verifies credentials and issues a JWT bearer token carrying the role claim.
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    /**
     * FR-03: self-service password change for the currently authenticated principal,
     * identified by email (the JWT subject / username).
     */
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("No user found with email " + email));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
