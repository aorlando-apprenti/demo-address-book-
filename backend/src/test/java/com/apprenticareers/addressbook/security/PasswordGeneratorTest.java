package com.apprenticareers.addressbook.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordGeneratorTest {

    @Test
    void generate_defaultLengthIsTwelve() {
        assertThat(PasswordGenerator.generate()).hasSize(12);
    }

    @Test
    void generate_respectsRequestedLength() {
        assertThat(PasswordGenerator.generate(20)).hasSize(20);
    }

    @Test
    void generate_rejectsNonPositiveLength() {
        assertThrows(IllegalArgumentException.class, () -> PasswordGenerator.generate(0));
    }

    @Test
    void generate_producesDifferentValuesAcrossCalls() {
        String first = PasswordGenerator.generate();
        String second = PasswordGenerator.generate();

        assertThat(first).isNotEqualTo(second);
    }
}
