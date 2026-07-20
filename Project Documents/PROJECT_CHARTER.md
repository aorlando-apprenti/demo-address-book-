# Project Charter: demo-address-book

## 1. Project Description
An online application that holds names, addresses, telephone numbers, and email addresses for a user's contacts. Built as a small test application to verify the end-to-end SDLC agent pipeline (Business Analyst → Software Architect → Software Developer → Code Reviewer → QA Tester → DevOps).

## 2. Success Criteria
- Users can successfully add, edit, delete, and search contacts.
- Users can register with an Email, Address, and Telephone Number; the User Name is the email address.
- Every user account has a password.
- Admins can add and remove user accounts.
- An initial Admin account name and password are generated at setup.
- Admins can change their own password via self-service.
- Admins can reset a user's password by generating a new password for them.
- Milestone 1 (User/Admin Access) is independently testable before Milestone 2 (contact management) begins.

## 3. High-Level Scope
**In-Scope:**
- Contact management: add, edit, delete, search contacts (name, address, telephone, email).
- User registration (email as username, address, telephone number, password).
- Initial auto-generated Admin account (name + password).
- Self-service password change for Admin (and, by extension, users).
- Admin capability to add and remove user accounts.
- Admin capability to reset a user's password (generate new password).

**Out-of-Scope:**
- OAuth / SSO authentication.
- Multi-tenant organizations.
- Contact import/export.
- Mobile application.

## 4. Milestones & Timeline
- **Milestone 1 — Initial Build: User & Admin Access (Iteration 1: Foundation & Security):** Deliver end-to-end user registration, login authentication, admin account generation, self-service password change, and admin-driven user add/remove/password-reset. This milestone must be independently testable (user and admin access flows) before Milestone 2 begins.
- **Milestone 2 — Main Functionality (Iteration 2: Contact Management):** Deliver contact add/edit/delete/search functionality for authenticated users.

## 5. Roles & Responsibilities (SDLC AI Project Team)
The project execution matrix is defined below. The human user drives the vision while the automated agent pipeline executes the lifecycle:

*   **Product Owner (The User):** Ultimate stakeholder. Answers prompt phases, defines high-level vision, sets acceptance criteria, and signs off on deliverables.
*   **Project Manager Agent (`@project-manager`):** Interprets this charter, constructs milestone plans, breaks down features into tasks, and coordinates work delegation across sub-agents.
*   **Business Analyst Agent (`@business-analyst`):** Follows the BABOK framework to extract system requirements from the scope and creates the `Requirements.md` file.
*   **Software Architect Agent (`@software-architect`):** Creates system architecture, patterns, component diagrams, and layout blueprints in `Architecture.md` based on requirements.
*   **Software Developer Agent (`@software-developer`):** Implements clean code and unit tests following the architectural blueprints.
*   **Code Reviewer Agent (`@code-reviewer`):** Audits newly written code against architectural standards, security principles, and gives final production approval.
*   **QA Tester Agent (`@qa-tester`):** Generates and runs external end-to-end frontend tests to verify user-facing requirements match criteria.
*   **DevOps Agent (`@dev-ops`):** Manages environments, compiles production builds, containerizes components, and coordinates deployment steps.
