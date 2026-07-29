package com.apprenticareers.addressbook.repository;

import com.apprenticareers.addressbook.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User newUser(String email, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hashed-password");
        user.setAddressLine1("123 Main St");
        user.setCity("Springfield");
        user.setState("IL");
        user.setZipCode("62701");
        user.setTelephoneNumber("555-0100");
        user.setRole(role);
        return user;
    }

    @Test
    void savesAndAutoPopulatesCreatedAt() {
        User saved = userRepository.save(newUser("alice@example.com", User.Role.USER));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByEmail_returnsMatchingUser() {
        userRepository.save(newUser("bob@example.com", User.Role.USER));

        Optional<User> found = userRepository.findByEmail("bob@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("bob@example.com");
    }

    @Test
    void findByEmail_returnsEmptyWhenNoMatch() {
        Optional<User> found = userRepository.findByEmail("nobody@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_trueAndFalseCases() {
        userRepository.save(newUser("carol@example.com", User.Role.USER));

        assertThat(userRepository.existsByEmail("carol@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("unknown@example.com")).isFalse();
    }

    @Test
    void existsByRole_detectsAdminPresence() {
        assertThat(userRepository.existsByRole(User.Role.ADMIN)).isFalse();

        userRepository.save(newUser("admin@example.com", User.Role.ADMIN));

        assertThat(userRepository.existsByRole(User.Role.ADMIN)).isTrue();
    }

    @Test
    void emailUniqueConstraint_isEnforcedAtColumnLevel() {
        userRepository.save(newUser("dupe@example.com", User.Role.USER));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            userRepository.saveAndFlush(newUser("dupe@example.com", User.Role.USER));
        });
    }
}
