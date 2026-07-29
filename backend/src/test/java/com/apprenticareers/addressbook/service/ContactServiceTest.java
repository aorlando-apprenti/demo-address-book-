package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.Contact;
import com.apprenticareers.addressbook.dto.ContactRequest;
import com.apprenticareers.addressbook.dto.ContactResponse;
import com.apprenticareers.addressbook.exception.ContactNotFoundException;
import com.apprenticareers.addressbook.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    private Contact existingContact;

    @BeforeEach
    void setUp() {
        existingContact = new Contact();
        existingContact.setId(100L);
        existingContact.setOwnerUserId(1L);
        existingContact.setName("Alice Smith");
        existingContact.setAddressLine1("1 Elm St");
        existingContact.setAddressLine2(null);
        existingContact.setCity("Springfield");
        existingContact.setState("IL");
        existingContact.setZipCode("62701");
        existingContact.setTelephoneNumber("555-0100");
        existingContact.setEmail("alice@example.com");
        existingContact.setCreatedAt(LocalDateTime.now());
        existingContact.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void addContact_persistsContactScopedToOwner() {
        ContactRequest request = new ContactRequest("Bob Jones", "2 Oak Ave", null, "Springfield", "IL", "62701", "555-0200", "bob@example.com");
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact c = invocation.getArgument(0);
            c.setId(101L);
            return c;
        });

        ContactResponse response = contactService.addContact(1L, request);

        ArgumentCaptor<Contact> captor = ArgumentCaptor.forClass(Contact.class);
        verify(contactRepository).save(captor.capture());
        Contact saved = captor.getValue();

        assertThat(saved.getOwnerUserId()).isEqualTo(1L);
        assertThat(saved.getName()).isEqualTo("Bob Jones");
        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getName()).isEqualTo("Bob Jones");
    }

    @Test
    void getContacts_returnsOnlyOwnersContacts() {
        when(contactRepository.findByOwnerUserId(1L)).thenReturn(List.of(existingContact));

        List<ContactResponse> results = contactService.getContacts(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Alice Smith");
    }

    @Test
    void updateContact_updatesFieldsWhenOwnedByCaller() {
        ContactRequest request = new ContactRequest("Alice Updated", "9 New St", null, "Springfield", "IL", "62701", "555-9999", "alice.new@example.com");
        when(contactRepository.findByIdAndOwnerUserId(100L, 1L)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContactResponse response = contactService.updateContact(100L, 1L, request);

        assertThat(response.getName()).isEqualTo("Alice Updated");
        assertThat(response.getAddressLine1()).isEqualTo("9 New St");
        verify(contactRepository).save(existingContact);
    }

    @Test
    void updateContact_throwsWhenContactNotOwnedByCaller() {
        ContactRequest request = new ContactRequest("Hijacked", "x", "x", "x", "x", "x", "x", "x@example.com");
        when(contactRepository.findByIdAndOwnerUserId(100L, 2L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> contactService.updateContact(100L, 2L, request));
        verify(contactRepository, never()).save(any());
    }

    @Test
    void deleteContact_deletesWhenOwnedByCaller() {
        when(contactRepository.findByIdAndOwnerUserId(100L, 1L)).thenReturn(Optional.of(existingContact));

        contactService.deleteContact(100L, 1L);

        verify(contactRepository).delete(existingContact);
    }

    @Test
    void deleteContact_throwsWhenContactNotOwnedByCaller() {
        when(contactRepository.findByIdAndOwnerUserId(100L, 2L)).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> contactService.deleteContact(100L, 2L));
        verify(contactRepository, never()).delete(any());
    }

    @Test
    void searchContacts_delegatesToMultiFieldSearchWhenTermProvided() {
        when(contactRepository.searchByOwnerUserIdAndTerm(1L, "alice")).thenReturn(List.of(existingContact));

        List<ContactResponse> results = contactService.searchContacts(1L, "alice");

        assertThat(results).hasSize(1);
        verify(contactRepository).searchByOwnerUserIdAndTerm(1L, "alice");
        verify(contactRepository, never()).findByOwnerUserId(any());
    }

    @Test
    void searchContacts_returnsAllOwnedContactsWhenTermBlank() {
        when(contactRepository.findByOwnerUserId(1L)).thenReturn(List.of(existingContact));

        List<ContactResponse> results = contactService.searchContacts(1L, "  ");

        assertThat(results).hasSize(1);
        verify(contactRepository).findByOwnerUserId(1L);
        verify(contactRepository, never()).searchByOwnerUserIdAndTerm(any(), any());
    }

    @Test
    void searchContacts_returnsAllOwnedContactsWhenTermNull() {
        when(contactRepository.findByOwnerUserId(1L)).thenReturn(List.of(existingContact));

        List<ContactResponse> results = contactService.searchContacts(1L, null);

        assertThat(results).hasSize(1);
        verify(contactRepository).findByOwnerUserId(1L);
    }
}
