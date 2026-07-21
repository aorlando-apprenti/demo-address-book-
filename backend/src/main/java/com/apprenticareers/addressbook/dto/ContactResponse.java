package com.apprenticareers.addressbook.dto;

import com.apprenticareers.addressbook.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private Long id;
    private String name;
    private String address;
    private String telephoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContactResponse from(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getAddress(),
                contact.getTelephoneNumber(),
                contact.getEmail(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }
}
