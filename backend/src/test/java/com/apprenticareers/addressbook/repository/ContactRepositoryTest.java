package com.apprenticareers.addressbook.repository;

import com.apprenticareers.addressbook.domain.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    private Contact newContact(Long ownerUserId, String name, String address, String phone, String email) {
        Contact contact = new Contact();
        contact.setOwnerUserId(ownerUserId);
        contact.setName(name);
        contact.setAddressLine1(address);
        contact.setTelephoneNumber(phone);
        contact.setEmail(email);
        return contact;
    }

    @Test
    void savesAndAutoPopulatesTimestamps() {
        Contact saved = contactRepository.save(newContact(1L, "Alice", "1 Elm St", "555-0100", "alice@example.com"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void findByOwnerUserId_returnsOnlyThatUsersContacts() {
        contactRepository.save(newContact(1L, "Alice", "1 Elm St", "555-0100", "alice@example.com"));
        contactRepository.save(newContact(2L, "Bob", "2 Oak St", "555-0200", "bob@example.com"));

        List<Contact> ownerOneContacts = contactRepository.findByOwnerUserId(1L);

        assertThat(ownerOneContacts).hasSize(1);
        assertThat(ownerOneContacts.get(0).getName()).isEqualTo("Alice");
    }

    @Test
    void findByIdAndOwnerUserId_returnsEmptyForWrongOwner() {
        Contact saved = contactRepository.save(newContact(1L, "Alice", "1 Elm St", "555-0100", "alice@example.com"));

        Optional<Contact> correctOwner = contactRepository.findByIdAndOwnerUserId(saved.getId(), 1L);
        Optional<Contact> wrongOwner = contactRepository.findByIdAndOwnerUserId(saved.getId(), 2L);

        assertThat(correctOwner).isPresent();
        assertThat(wrongOwner).isEmpty();
    }

    @Test
    void searchByOwnerUserIdAndTerm_matchesAcrossAllFieldsCaseInsensitively() {
        contactRepository.save(newContact(1L, "Alice Smith", "1 Elm St", "555-0100", "alice@example.com"));
        contactRepository.save(newContact(1L, "Bob Jones", "2 Oak Ave", "555-0200", "bob@example.com"));
        contactRepository.save(newContact(2L, "Alice Clone", "3 Pine Rd", "555-0300", "alice2@example.com"));

        assertThat(contactRepository.searchByOwnerUserIdAndTerm(1L, "alice"))
                .extracting(Contact::getName).containsExactly("Alice Smith");
        assertThat(contactRepository.searchByOwnerUserIdAndTerm(1L, "OAK"))
                .extracting(Contact::getName).containsExactly("Bob Jones");
        assertThat(contactRepository.searchByOwnerUserIdAndTerm(1L, "555-02"))
                .extracting(Contact::getName).containsExactly("Bob Jones");
        assertThat(contactRepository.searchByOwnerUserIdAndTerm(1L, "bob@example"))
                .extracting(Contact::getName).containsExactly("Bob Jones");
    }

    @Test
    void searchByOwnerUserIdAndTerm_neverReturnsAnotherUsersContacts() {
        contactRepository.save(newContact(2L, "Alice Clone", "3 Pine Rd", "555-0300", "alice2@example.com"));

        List<Contact> results = contactRepository.searchByOwnerUserIdAndTerm(1L, "alice");

        assertThat(results).isEmpty();
    }

    @Test
    void deleteByOwnerUserId_removesAllContactsForThatUser() {
        contactRepository.save(newContact(1L, "Alice", "1 Elm St", "555-0100", "alice@example.com"));
        contactRepository.save(newContact(1L, "Alice Two", "2 Elm St", "555-0101", "alice2@example.com"));
        contactRepository.save(newContact(2L, "Bob", "2 Oak St", "555-0200", "bob@example.com"));

        contactRepository.deleteByOwnerUserId(1L);

        assertThat(contactRepository.findByOwnerUserId(1L)).isEmpty();
        assertThat(contactRepository.findByOwnerUserId(2L)).hasSize(1);
    }
}
