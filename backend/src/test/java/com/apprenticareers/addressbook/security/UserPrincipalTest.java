package com.apprenticareers.addressbook.security;

import com.apprenticareers.addressbook.domain.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserPrincipalTest {

    @Test
    void from_mapsUserFieldsAndRoleAuthority() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPasswordHash("hash");
        user.setRole(User.Role.ADMIN);
        user.setCreatedAt(LocalDateTime.now());

        UserPrincipal principal = UserPrincipal.from(user);

        assertThat(principal.getId()).isEqualTo(1L);
        assertThat(principal.getUsername()).isEqualTo("user@example.com");
        assertThat(principal.getPassword()).isEqualTo("hash");
        assertThat(principal.getRole()).isEqualTo(User.Role.ADMIN);
        assertThat(principal.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_ADMIN");
    }

    @Test
    void accountStatusFlags_areAllTrue() {
        UserPrincipal principal = new UserPrincipal(1L, "u@example.com", "hash", User.Role.USER);

        assertThat(principal.isAccountNonExpired()).isTrue();
        assertThat(principal.isAccountNonLocked()).isTrue();
        assertThat(principal.isCredentialsNonExpired()).isTrue();
        assertThat(principal.isEnabled()).isTrue();
    }
}
