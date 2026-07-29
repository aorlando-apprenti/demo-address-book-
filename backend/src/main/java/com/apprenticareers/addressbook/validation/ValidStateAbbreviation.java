package com.apprenticareers.addressbook.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that a field is a real USPS state/territory abbreviation (Publication 28 §358),
 * not merely two uppercase letters. Null or blank values are considered valid — pair this
 * with {@code @NotBlank} on fields where the value is actually required (per CR-001,
 * {@code User}'s address is required but {@code Contact}'s is optional).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StateAbbreviationValidator.class)
public @interface ValidStateAbbreviation {

    String message() default "must be a valid USPS state/territory abbreviation";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
