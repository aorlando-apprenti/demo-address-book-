package com.apprenticareers.addressbook.dto;

import com.apprenticareers.addressbook.validation.ValidStateAbbreviation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Address Line 1 is required")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    @ValidStateAbbreviation
    private String state;

    @NotBlank(message = "ZIP Code is required")
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "ZIP Code must be 5 digits, optionally extended to ZIP+4 (#####-####)")
    private String zipCode;

    @NotBlank(message = "Telephone number is required")
    private String telephoneNumber;
}
