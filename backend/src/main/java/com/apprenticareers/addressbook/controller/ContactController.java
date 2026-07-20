package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    // Implementation to follow in Iteration 1 tasks
    // @PostMapping - addContact(ContactRequest)
    // @GetMapping - getContacts()
    // @PutMapping("/{id}") - updateContact(id, ContactRequest)
    // @DeleteMapping("/{id}") - deleteContact(id)
    // @GetMapping("/search") - searchContacts(query)
}
