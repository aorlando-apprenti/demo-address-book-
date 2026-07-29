package com.apprenticareers.addressbook.dto;

import com.apprenticareers.addressbook.validation.ValidStateAbbreviation;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FR-07/FR-08: payload for creating or editing a contact. Deliberately has no
 * "ownerUserId" field: ownership is always derived server-side from the
 * authenticated principal, never trusted from the client.
 * <p>
 * Per CR-001, all 5 structured address fields are optional here (matching the
 * pre-existing single {@code address} field's optionality) — a contact may
 * have no address on file at all — but {@code state}/{@code zipCode} are still
 * format-validated whenever a value is actually supplied.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String addressLine1;

    private String addressLine2;

    private String city;

    @ValidStateAbbreviation
    private String state;

    @Pattern(regexp = "^(\\d{5}(-\\d{4})?)?$", message = "ZIP Code must be 5 digits, optionally extended to ZIP+4 (#####-####)")
    private String zipCode;

    private String telephoneNumber;

    @Email(message = "Email must be a valid address")
    private String email;
}
