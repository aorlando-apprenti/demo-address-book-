package com.apprenticareers.addressbook.controller;

import com.apprenticareers.addressbook.dto.ContactRequest;
import com.apprenticareers.addressbook.dto.ContactResponse;
import com.apprenticareers.addressbook.dto.MessageResponse;
import com.apprenticareers.addressbook.security.UserPrincipal;
import com.apprenticareers.addressbook.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FR-07–FR-11: CRUD + search over the authenticated user's own contacts.
 * No {@code @PreAuthorize} role restriction is required beyond authentication
 * (enforced globally by {@code SecurityConfig}) — any authenticated USER or
 * ADMIN manages only their own contacts, per Architecture §1 RBAC table.
 * The owning user id is always derived from the security principal, never
 * from client input.
 */
@RestController
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponse> addContact(Authentication authentication,
                                                        @Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.addContact(ownerUserId(authentication), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ContactResponse>> getContacts(Authentication authentication) {
        return ResponseEntity.ok(contactService.getContacts(ownerUserId(authentication)));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactResponse>> searchContacts(Authentication authentication,
                                                                  @RequestParam(required = false) String query) {
        return ResponseEntity.ok(contactService.searchContacts(ownerUserId(authentication), query));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateContact(Authentication authentication,
                                                           @PathVariable Long id,
                                                           @Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.updateContact(id, ownerUserId(authentication), request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteContact(Authentication authentication, @PathVariable Long id) {
        contactService.deleteContact(id, ownerUserId(authentication));
        return ResponseEntity.ok(new MessageResponse("Contact deleted successfully."));
    }

    private Long ownerUserId(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getId();
    }
}
