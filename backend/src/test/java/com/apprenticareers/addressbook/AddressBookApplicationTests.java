package com.apprenticareers.addressbook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test verifying the full application context (security, JPA,
 * services, controllers, startup admin seeder) wires up without error.
 */
@SpringBootTest
class AddressBookApplicationTests {

    @Test
    void contextLoads() {
        // Intentionally empty: a successful context load is the assertion.
    }
}
