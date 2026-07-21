package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.User;
import com.apprenticareers.addressbook.repository.UserRepository;
import com.apprenticareers.addressbook.security.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupAdminSeederService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.seed.email:admin@addressbook.local}")
    private String seedAdminEmail;

    @Value("${admin.seed.address:HQ}")
    private String seedAdminAddress;

    @Value("${admin.seed.telephone:000-000-0000}")
    private String seedAdminTelephone;

    /**
     * FR-02: creates the initial Admin account if no ADMIN account exists yet.
     * The generated plaintext password is logged once for operator retrieval
     * and is never persisted or logged in plaintext again.
     */
    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(User.Role.ADMIN)) {
            log.info("Startup admin seeding skipped: an ADMIN account already exists.");
            return;
        }

        String generatedPassword = PasswordGenerator.generate();

        User admin = new User();
        admin.setEmail(seedAdminEmail);
        admin.setPasswordHash(passwordEncoder.encode(generatedPassword));
        admin.setAddress(seedAdminAddress);
        admin.setTelephoneNumber(seedAdminTelephone);
        admin.setRole(User.Role.ADMIN);

        userRepository.save(admin);

        log.info("==================================================================");
        log.info("Initial ADMIN account created. email={} password={}", seedAdminEmail, generatedPassword);
        log.info("Store this password securely now; it will not be logged again.");
        log.info("==================================================================");
    }
}
