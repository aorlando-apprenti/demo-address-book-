# System Architecture: demo-address-book

## Document Version History
| Version | Date | CR ID | Change Description |
| :--- | :--- | :--- | :--- |
| v1.0 | 2026-07-20 | (original) | Initial approved architecture for demo-address-book |
| v1.1 | 2026-07-29 | CR-001 | Expanded `User.address`/`Contact.address` into structured fields (`addressLine1`, `addressLine2`, `city`, `state`, `zipCode`); updated schema, search, and registration/contact sequencing accordingly |

## 1. Security and User Access

*   **Authentication & Authorization Strategy:**
    - Spring Security with stateless, token-based authentication (JWT bearer tokens) issued on successful login.
    - Username = email address (per FR-01). Passwords are hashed at rest using BCrypt (`spring-boot-starter-security` `PasswordEncoder`); no plaintext storage anywhere (NFR: Data Security).
    - The initial Admin account (FR-02) is generated at application startup (e.g., a `CommandLineRunner`/data-seeding component) with a random generated password logged/output once for the operator to retrieve; the Admin must be able to self-service-change it afterward (FR-03).
    - Password reset by Admin (FR-06) generates a new random password for the target user and invalidates the old one; it does not require the old password.

*   **Role-Based Access Control (RBAC):**
    | Role | Access Paths |
    | :--- | :--- |
    | **USER** | `POST /auth/register`, `POST /auth/login`, `PUT /account/password` (self), `GET/POST/PUT/DELETE /contacts/**` scoped strictly to contacts they own (FR-07–FR-11). No access to `/admin/**`. |
    | **ADMIN** | `POST /auth/login`, `PUT /account/password` (self), `POST /admin/users`, `DELETE /admin/users/{id}`, `POST /admin/users/{id}/reset-password` (FR-04–FR-06). No access to any other user's `/contacts/**` data — Admin scope is limited to account management, not contact data, per Requirements. |
    - Enforced via method-level security (`@PreAuthorize`) checked against the authenticated principal's role and, for contacts, an explicit ownership check (`contact.ownerId == principal.id`) at the Service layer — never trusting a client-supplied user ID.

## 2. Core Business Entities
*   **User:** `id`, `email` (unique, used as username), `passwordHash`, `addressLine1`, `addressLine2` (nullable), `city`, `state`, `zipCode`, `telephoneNumber`, `role` (`USER` | `ADMIN`), `createdAt`. *(CR-001: replaces the single `address` field with structured components — see Requirements.md §7.)*
*   **Contact:** `id`, `ownerUserId` (FK → User.id), `name`, `addressLine1`, `addressLine2` (nullable), `city`, `state`, `zipCode`, `telephoneNumber`, `email`, `createdAt`, `updatedAt`. *(CR-001: same field expansion as `User`.)*

## 3. Data Persistence
*   **Storage Paradigm:** Relational (H2, file-mode) for this test application — self-contained, no external DB dependency required to verify the pipeline. Swappable to PostgreSQL/MySQL for a real production deployment via Spring Data JPA without code changes (only `application.properties` datasource config changes).
*   **Database Schema Guidelines:**
    - `users.email` — `UNIQUE NOT NULL` index (login lookups and username enforcement).
    - `contacts.owner_user_id` — `NOT NULL` foreign key with an index (per-user contact scoping and search performance, FR-11).
    - `contacts` — composite/secondary indexes on `(owner_user_id, name)` to support multi-field search (FR-10) at reasonable performance for a test app.
    - Cascade rule: deleting a `User` (FR-05) cascades to delete their owned `Contact` rows.
    - **CR-001 Schema Delta:** `users` and `contacts` each drop the single `address VARCHAR` column and add five columns: `address_line1 VARCHAR NOT NULL`, `address_line2 VARCHAR NULL`, `city VARCHAR NOT NULL`, `state CHAR(2) NOT NULL`, `zip_code VARCHAR(10) NOT NULL` (sized for ZIP+4's `#####-####`). This is a notable schema change, not a routine edit — called out explicitly per change-request convention.
    - **Correction found during CR1-TSK-9 manual verification:** the original note here claimed "no migration script is required... the schema regenerates from the updated `@Entity` classes on next startup." That's only true for a *fresh* database. `spring.jpa.hibernate.ddl-auto=update` does an in-place `ALTER TABLE ... ADD COLUMN ... NOT NULL` against the *existing* persistent H2 file — it does not drop and recreate. Since the local `backend/data/addressbook.mv.db` already had rows (the seeded Admin account) from prior sessions, the `NOT NULL` column add failed with a constraint violation on startup. **Fix applied:** the stale local `backend/data/*.db` files were deleted (git-ignored, disposable test data per this project's own convention) so the schema regenerates cleanly from empty. **This is not a durable fix** — the same failure will recur any time `ddl-auto=update` needs to add a `NOT NULL` column to a table that already has rows. A real migration tool (Flyway/Liquibase, which can express "add nullable → backfill → alter not-null" as separate steps) would resolve this properly; that's out of scope for CR-001 and should be raised as its own follow-up Change Request if this project continues to evolve its schema.

## 4. Services
*   **AuthService:** Handles registration (hash + persist new `User`), login (credential verification, JWT issuance), and self-service password change.
*   **AdminUserService:** Handles Admin-only user creation, user removal, and password-reset-generation for a target user. Enforces `ADMIN`-only access via `@PreAuthorize`.
*   **ContactService:** Handles add/edit/delete/search of `Contact` records, always scoped to the authenticated user's `ownerUserId`; search queries filter across `name`, `addressLine1`, `addressLine2`, `city`, `state`, `zipCode`, `telephoneNumber`, `email` (FR-10, updated per CR-001).
*   **StartupAdminSeederService:** One-time bootstrap component that creates the initial Admin account if none exists (FR-02).

## 5. Sequencing of Operations
1. **Registration:** Client → `POST /auth/register` (email, password, addressLine1, addressLine2, city, state, zipCode, phone — updated per CR-001) → `AuthController` validates payload → `AuthService` checks email uniqueness → hashes password → `UserRepository` persists → returns confirmation.
2. **Login:** Client → `POST /auth/login` (email, password) → `AuthController` → `AuthService` verifies hash via `PasswordEncoder` → issues JWT with embedded role claim → client stores token for subsequent requests.
3. **Contact CRUD/Search:** Client → `POST/PUT/DELETE/GET /contacts/**` (with bearer token) → `ContactController` extracts authenticated user ID from the security context → `ContactService` enforces ownership → `ContactRepository` executes the query, always filtered by `ownerUserId`.
4. **Admin Add/Remove User:** Admin client → `POST/DELETE /admin/users/**` → `AdminUserController` (requires `ADMIN` role) → `AdminUserService` → `UserRepository` creates/removes the target `User` row (cascades to their `Contact` rows on removal).
5. **Admin Password Reset:** Admin client → `POST /admin/users/{id}/reset-password` → `AdminUserService` generates a new random password, hashes it, updates the target `User` row, and returns the new password to the Admin to relay to the affected user.
6. **Self-Service Password Change:** Authenticated client (User or Admin) → `PUT /account/password` → `AuthService` verifies current session identity, hashes and persists the new password.

## 6. System Topology
*   **Package Structure:**
    ```
    com.demoaddressbook
    ├── config          # Spring Security config, JWT filter, seeding bean
    ├── controller       # AuthController, ContactController, AdminUserController, AccountController
    ├── service          # AuthService, ContactService, AdminUserService
    ├── repository       # UserRepository, ContactRepository (Spring Data JPA)
    ├── domain           # User, Contact (@Entity)
    └── dto              # Request/response payload objects (RegisterRequest, ContactResponse, etc.)
    ```
*   **Domain Boundaries:** Two bounded contexts — **Identity & Access** (User, auth, admin user management) and **Contact Management** (Contact CRUD/search) — communicating only through the authenticated user's ID; Contact Management never queries User internals beyond the ID.
*   **Entity Mapping:** `User` ↔ `users` table; `Contact` ↔ `contacts` table, `Contact.ownerUserId` maps the FK relationship back to `users.id`.

## 7. Spring Framework Design: Monolithic Multi-Layered Pattern
*   **API / Presentation Layer:** `@RestController` classes (`AuthController`, `AccountController`, `ContactController`, `AdminUserController`) receive HTTP requests, validate JSON payloads (`@Valid`), and delegate to the Service layer.
*   **Service Layer:** `@Service` + `@Transactional` classes (`AuthService`, `ContactService`, `AdminUserService`) contain business rules, ownership/authorization checks, and password hashing logic.
*   **Data Access / Repository Layer:** `@Repository` Spring Data JPA interfaces (`UserRepository`, `ContactRepository`) provide persistence and query methods (e.g., `findByOwnerUserIdAndNameContaining`).
*   **Domain / Entity Layer:** `@Entity` POJOs (`User`, `Contact`) mapping directly to the relational schema described in Section 3.
