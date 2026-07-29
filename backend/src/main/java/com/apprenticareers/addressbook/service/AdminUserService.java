package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.dto.CreateUserRequest;
import com.apprenticareers.addressbook.dto.CreateUserResponse;
import com.apprenticareers.addressbook.dto.ResetPasswordResponse;
import com.apprenticareers.addressbook.dto.UserResponse;
import com.apprenticareers.addressbook.exception.EmailAlreadyExistsException;
import com.apprenticareers.addressbook.exception.UserNotFoundException;
import com.apprenticareers.addressbook.repository.ContactRepository;
import com.apprenticareers.addressbook.repository.UserRepository;
import com.apprenticareers.addressbook.security.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContactRepository contactRepository;

    /**
     * FR-04: Admin adds a new user account. Always provisioned with role USER
     * (no role is accepted from the request) to prevent privilege escalation.
     * A random temporary password is generated and returned for the Admin to relay.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public CreateUserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String temporaryPassword = PasswordGenerator.generate();

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        user.setAddressLine1(request.getAddressLine1());
        user.setAddressLine2(request.getAddressLine2());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setZipCode(request.getZipCode());
        user.setTelephoneNumber(request.getTelephoneNumber());
        user.setRole(User.Role.USER);

        User saved = userRepository.save(user);
        return new CreateUserResponse(UserResponse.from(saved), temporaryPassword);
    }

    /**
     * FR-05: Admin removes an existing user account. Cascades to delete the
     * user's owned {@code Contact} rows first (Architecture §3), since
     * {@code Contact.ownerUserId} is a plain FK column rather than a JPA
     * association with entity-level cascade.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void removeUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        contactRepository.deleteByOwnerUserId(user.getId());
        userRepository.delete(user);
    }

    /**
     * FR-06: Admin resets a user's password, generating and persisting a new
     * random password without requiring the user's old password.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ResetPasswordResponse resetUserPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newPassword = PasswordGenerator.generate();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return new ResetPasswordResponse(user.getId(), user.getEmail(), newPassword);
    }
}
