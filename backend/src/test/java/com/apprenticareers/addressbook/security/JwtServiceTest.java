package com.apprenticareers.addressbook.security;

import com.apprenticareers.addressbook.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "test-secret-key-must-be-at-least-32-bytes-long!!";

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 60_000L);

        user = new User();
        user.setId(42L);
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        user.setRole(User.Role.ADMIN);
    }

    @Test
    void generateToken_embedsSubjectRoleAndUserId() {
        String token = jwtService.generateToken(user);

        assertThat(jwtService.extractEmail(token)).isEqualTo("user@example.com");
        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
        assertThat(jwtService.extractUserId(token)).isEqualTo(42L);
    }

    @Test
    void isTokenValid_trueForMatchingUsername() {
        String token = jwtService.generateToken(user);
        UserPrincipal principal = UserPrincipal.from(user);

        assertThat(jwtService.isTokenValid(token, principal)).isTrue();
    }

    @Test
    void isTokenValid_falseForMismatchedUsername() {
        String token = jwtService.generateToken(user);
        UserPrincipal otherPrincipal = new UserPrincipal(99L, "other@example.com", "hash", User.Role.USER);

        assertThat(jwtService.isTokenValid(token, otherPrincipal)).isFalse();
    }

    @Test
    void isTokenValid_falseForExpiredToken() throws InterruptedException {
        JwtService shortLivedJwtService = new JwtService(SECRET, 1L);
        String token = shortLivedJwtService.generateToken(user);
        Thread.sleep(10);

        UserPrincipal principal = UserPrincipal.from(user);

        assertThat(shortLivedJwtService.isTokenValid(token, principal)).isFalse();
    }

    @Test
    void isTokenValid_falseForMalformedToken() {
        UserPrincipal principal = UserPrincipal.from(user);

        assertThat(jwtService.isTokenValid("not-a-real-token", principal)).isFalse();
    }
}
