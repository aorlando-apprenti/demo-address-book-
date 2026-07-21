package com.apprenticareers.addressbook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin-supplied payload for creating a new user account (FR-04).
 * Deliberately has no "role" field: accounts created through this endpoint
 * are always provisioned with role USER to prevent privilege escalation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid address")
    private String email;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Telephone number is required")
    private String telephoneNumber;
}
