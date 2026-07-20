package com.apprenticareers.addressbook.service;

import com.apprenticareers.addressbook.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    // Implementation to follow in Iteration 1 tasks
    // - addContact(ownerUserId, name, address, phone, email)
    // - updateContact(contactId, ownerUserId, name, address, phone, email)
    // - deleteContact(contactId, ownerUserId)
    // - getContactsByOwner(ownerUserId)
    // - searchContacts(ownerUserId, searchTerms)
}
