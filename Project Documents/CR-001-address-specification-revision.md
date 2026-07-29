# Change Request CR-001: Revision for Address Specification

*   **Date Submitted:** 2026-07-29
*   **Requested By:** Anthony Orlando
*   **Target Application:** demo-address-book
*   **Priority:** Medium
*   **Status:** Open

## Problem / Motivation
The address currently stored in the Address Book is a single free-text line. A proper address should be broken into structured components rather than one collapsed field.

## Proposed Change
Expand all relevant address entities/fields to include the missing structured components: **Address Line 1, Address Line 2, City, State, ZIP Code**. Field definitions, formatting, and abbreviations should follow USPS addressing standards (Publication 28: https://pe.usps.com/text/pub28/welcome.htm).

## Out-of-Scope
No other scope changes — this CR is limited to the address field structure/specification revision; no new entities, features, or unrelated modifications.

## Impact Assessment

### Requirements Impact (`@business-analyst`, Requirements.md v1.1)
*   **Entities affected:** `User` and `Contact` — both currently have a single free-text `address` field; both are expanded identically.
*   **Functional Requirements affected:** FR-01 (user registration address), FR-07 (contact creation address), FR-10 (contact search — now matches across all structured address fields, not just one line).
*   **New section added:** §7 Data Entity Field Specifications (this BRD predated that template section; this CR retroactively adds it, scoped to the address fields this change touches).
*   **New NFR added:** Data Validation & Standards (USPS state-abbreviation and ZIP/ZIP+4 format validation).

### Architecture Impact (`@software-architect`, Architecture.md v1.1 — COMPLETE)
*   `User` and `Contact` `@Entity` classes (Section 2) each replace their single `address` field with `addressLine1`, `addressLine2` (nullable), `city`, `state`, `zipCode`.
*   Database schema change (Section 3): `users` and `contacts` tables each drop `address` and add the 5 new columns. No migration script needed — H2 + Hibernate-managed DDL, no production data to preserve.
*   `ContactService` search (Section 4, FR-10) updated to filter across all 5 new address columns instead of one.
*   Registration sequencing (Section 5) updated to reflect the expanded payload.
*   DTOs (`dto` package, Section 6) will need the same field expansion when `@software-developer` implements this — not called out as a separate architectural decision since it's a direct, mechanical consequence of the entity change.
