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
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private String telephoneNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContactResponse from(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getAddressLine1(),
                contact.getAddressLine2(),
                contact.getCity(),
                contact.getState(),
                contact.getZipCode(),
                contact.getTelephoneNumber(),
                contact.getEmail(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }
}
