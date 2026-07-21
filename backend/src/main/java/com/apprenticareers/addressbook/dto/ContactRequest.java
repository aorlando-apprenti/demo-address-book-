package com.apprenticareers.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FR-07/FR-08: payload for creating or editing a contact. Deliberately has no
 * "ownerUserId" field: ownership is always derived server-side from the
 * authenticated principal, never trusted from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String address;

    private String telephoneNumber;

    @Email(message = "Email must be a valid address")
    private String email;
}
