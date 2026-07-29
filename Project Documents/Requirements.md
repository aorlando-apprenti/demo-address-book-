# Business Requirements Document (BRD)

## Document Version History
| Version | Date | CR ID | Change Description |
| :--- | :--- | :--- | :--- |
| v1.0 | 2026-07-20 | (original) | Initial approved BRD for demo-address-book |
| v1.1 | 2026-07-29 | CR-001 | Expanded `address` on both `User` and `Contact` into structured components (Address Line 1, Address Line 2, City, State, ZIP Code) per USPS Publication 28; added a Data Entity Field Specifications section (retroactively bringing this document in line with the standard BRD template) |

## 1. Executive Summary
*   **Project Overview:** demo-address-book is an online application for storing and managing personal contacts (name, address, telephone, email), with user registration and admin-managed accounts.
*   **Purpose & Business Justification:** This project exists to validate the end-to-end multi-agent SDLC pipeline (Business Analyst → Software Architect → Software Developer → Code Reviewer → QA Tester → DevOps) using a small, realistic multi-user application involving authentication, admin management, and CRUD functionality.
*   **Timeline & Budget Targets:** No formal budget. Two milestones per the Project Charter, no fixed calendar dates.

## 2. Project Objectives
*   **Objective 1 (Milestone 1 — Foundation & Security):** 100% of user registration, login authentication, initial admin account generation, self-service password change, admin add/remove user, and admin password-reset functions pass QA before Milestone 2 begins.
*   **Objective 2 (Milestone 2 — Contact Management):** 100% of contact add/edit/delete/search functions (scoped privately per user) pass QA.

## 3. Project Scope
*   **In-Scope Boundaries:**
    - User registration (email as username, password, address, telephone number).
    - Auto-generated initial Admin account (name + password) at system setup.
    - Self-service password change for any authenticated account.
    - Admin add and remove user accounts.
    - Admin reset a user's password (generates a new password).
    - Authenticated users can add, edit, delete, and search their own contacts.
    - Contact search by name, address, telephone, or email.
    - Contacts are private per user — no cross-user visibility.
*   **Out-of-Scope Boundaries:**
    - OAuth / SSO authentication.
    - Multi-tenant organizations.
    - Contact import/export.
    - Mobile application.

## 4. Stakeholder Mapping
| Stakeholder / Persona | Role in Project | Level of Involvement / Impact |
| :--- | :--- | :--- |
| **Product Owner (User)** | Vision, Sign-off, Acceptance Gates | High / Ultimate Approver |
| **End User** | Registers an account, logs in, manages own private contacts, changes own password | High |
| **Admin** | Adds/removes user accounts, resets user passwords, has own login and password self-service | High |

## 5. Business Needs Statement
*   **Problem / Opportunity Statement:** No production business problem is being solved directly — this is a net-new build whose purpose is to exercise and validate the automated SDLC agent pipeline against a realistic small application.
*   **Current Process & Context:** Greenfield project; no legacy system or manual process is being replaced.

## 6. Functional Requirements
*   **FR-01:** A user can register an account using their email address (as the username), a password, a structured mailing address (Address Line 1, Address Line 2 [optional], City, State, ZIP Code — see §7 Data Entity Field Specifications), and a telephone number.
*   **FR-02:** An initial Admin account (name + password) is auto-generated at system setup.
*   **FR-03:** Any authenticated account (User or Admin) can change their own password via self-service.
*   **FR-04:** An Admin can add a new user account.
*   **FR-05:** An Admin can remove an existing user account.
*   **FR-06:** An Admin can reset a user's password by generating a new password for that user.
*   **FR-07:** An authenticated user can add a new contact (name, structured address [Address Line 1, Address Line 2 (optional), City, State, ZIP Code — see §7 Data Entity Field Specifications], telephone, email).
*   **FR-08:** An authenticated user can edit an existing contact they own.
*   **FR-09:** An authenticated user can delete an existing contact they own.
*   **FR-10:** An authenticated user can search their own contacts by name, address (matching across any of the structured address fields), telephone, or email.
*   **FR-11:** Contacts are private per user — a user may only view, edit, delete, or search their own contact records, never another user's.

## 7. Data Entity Field Specifications
Both `User.address` and `Contact.address` (previously a single free-text line) are expanded into the following structured components, per USPS Publication 28 (https://pe.usps.com/text/pub28/welcome.htm) — identical shape on both entities:

| Entity | Field | Structure | Format / Validation |
| :--- | :--- | :--- | :--- |
| User | addressLine1 | Required, single line | Primary delivery address line (street number + street name, plus any directional prefix/suffix), per USPS Pub 28 §23 |
| User | addressLine2 | Optional, single line | Secondary address unit designator (Apt, Suite, Floor, etc.), per USPS Pub 28 §213 |
| User | city | Required | City name |
| User | state | Required | 2-letter USPS state abbreviation, per USPS Pub 28 §358 |
| User | zipCode | Required | 5-digit ZIP Code, optionally extended to ZIP+4 format (`#####-####`) |
| Contact | addressLine1 | Required, single line | Same definition as `User.addressLine1` |
| Contact | addressLine2 | Optional, single line | Same definition as `User.addressLine2` |
| Contact | city | Required | Same definition as `User.city` |
| Contact | state | Required | Same definition as `User.state` |
| Contact | zipCode | Required | Same definition as `User.zipCode` |

## 8. Non-Functional Requirements
*   **Data Security & Compliance:** Passwords must be hashed at rest (no plaintext storage). No formal complexity policy is required. Authorization checks must ensure a user can only access their own contact records.
*   **Performance & Scalability:** No formal SLA. Reasonable responsiveness is expected for a small test application.
*   **Usability & Accessibility:** No formal accessibility standard required. A basic, usable UI is sufficient for this test application.
*   **Data Validation & Standards:** `state` must validate against the USPS standard state abbreviation list (Pub 28 §358); `zipCode` must validate against the 5-digit or ZIP+4 format; `addressLine1`, `city`, and `state` are required on both `User` and `Contact`, `addressLine2` is optional on both.

## 9. Assumptions & Constraints
*   **Technical / Resource Constraints:** Backend implemented in Java/Spring Boot; frontend implemented in Node/NPM, per the standing architecture-design and pipeline-deploy scaffolding conventions. No OAuth/SSO, multi-tenancy, import/export, or mobile requirements.
*   **Baseline Assumptions:** This is an internal test project used to validate the multi-agent SDLC pipeline; the only external stakeholder is the Product Owner (the user).
