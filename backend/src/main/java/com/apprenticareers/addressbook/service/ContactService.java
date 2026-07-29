package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.domain.Contact;
import com.apprenticareers.addressbook.dto.ContactRequest;
import com.apprenticareers.addressbook.dto.ContactResponse;
import com.apprenticareers.addressbook.exception.ContactNotFoundException;
import com.apprenticareers.addressbook.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FR-07–FR-11: add/edit/delete/search of {@link Contact} records, always
 * scoped to the authenticated user's {@code ownerUserId}. The caller
 * (ContactController) is responsible for deriving {@code ownerUserId} from
 * the authenticated security principal — this service never accepts a
 * client-supplied user id as the scoping key.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    /** FR-07: add a new contact owned by the authenticated user. */
    public ContactResponse addContact(Long ownerUserId, ContactRequest request) {
        Contact contact = new Contact();
        contact.setOwnerUserId(ownerUserId);
        contact.setName(request.getName());
        contact.setAddressLine1(request.getAddressLine1());
        contact.setAddressLine2(request.getAddressLine2());
        contact.setCity(request.getCity());
        contact.setState(request.getState());
        contact.setZipCode(request.getZipCode());
        contact.setTelephoneNumber(request.getTelephoneNumber());
        contact.setEmail(request.getEmail());

        Contact saved = contactRepository.save(contact);
        return ContactResponse.from(saved);
    }

    /** List all contacts owned by the authenticated user. */
    @Transactional(readOnly = true)
    public List<ContactResponse> getContacts(Long ownerUserId) {
        return contactRepository.findByOwnerUserId(ownerUserId).stream()
                .map(ContactResponse::from)
                .toList();
    }

    /**
     * FR-08/FR-11: edit an existing contact. The lookup is scoped by both id
     * and ownerUserId so a user can never edit another user's contact — an
     * out-of-scope id resolves identically to a non-existent id (404).
     */
    public ContactResponse updateContact(Long contactId, Long ownerUserId, ContactRequest request) {
        Contact contact = contactRepository.findByIdAndOwnerUserId(contactId, ownerUserId)
                .orElseThrow(() -> new ContactNotFoundException(contactId));

        contact.setName(request.getName());
        contact.setAddressLine1(request.getAddressLine1());
        contact.setAddressLine2(request.getAddressLine2());
        contact.setCity(request.getCity());
        contact.setState(request.getState());
        contact.setZipCode(request.getZipCode());
        contact.setTelephoneNumber(request.getTelephoneNumber());
        contact.setEmail(request.getEmail());

        Contact saved = contactRepository.save(contact);
        return ContactResponse.from(saved);
    }

    /** FR-09/FR-11: delete an existing contact owned by the authenticated user. */
    public void deleteContact(Long contactId, Long ownerUserId) {
        Contact contact = contactRepository.findByIdAndOwnerUserId(contactId, ownerUserId)
                .orElseThrow(() -> new ContactNotFoundException(contactId));
        contactRepository.delete(contact);
    }

    /**
     * FR-10: search the authenticated user's own contacts across name, the 5
     * structured address fields (addressLine1, addressLine2, city, state,
     * zipCode), telephoneNumber, and email (CR-001). A blank/absent term
     * returns all of the user's contacts.
     */
    @Transactional(readOnly = true)
    public List<ContactResponse> searchContacts(Long ownerUserId, String term) {
        List<Contact> results = (term == null || term.isBlank())
                ? contactRepository.findByOwnerUserId(ownerUserId)
                : contactRepository.searchByOwnerUserIdAndTerm(ownerUserId, term.trim());

        return results.stream()
                .map(ContactResponse::from)
                .toList();
    }
}
