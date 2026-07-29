package com.apprenticareers.addressbook.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

/**
 * Backs {@link ValidStateAbbreviation}: checks membership in the fixed set of USPS
 * state/territory/military abbreviations from Publication 28 §358, rather than just
 * checking "two uppercase letters" (which would wrongly accept e.g. "ZZ").
 */
public class StateAbbreviationValidator implements ConstraintValidator<ValidStateAbbreviation, String> {

    private static final Set<String> VALID_ABBREVIATIONS = Set.of(
            // 50 states
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY",
            // District of Columbia
            "DC",
            // USPS-recognized territories
            "AS", "GU", "MP", "PR", "VI",
            // Military "state" codes (APO/FPO/DPO addresses)
            "AA", "AE", "AP"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // optionality is enforced separately (e.g. @NotBlank), not here
        }
        return VALID_ABBREVIATIONS.contains(value.toUpperCase());
    }
}
