# System Architecture: demo-address-book

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
*   **User:** `id`, `email` (unique, used as username), `passwordHash`, `address`, `telephoneNumber`, `role` (`USER` | `ADMIN`), `createdAt`.
*   **Contact:** `id`, `ownerUserId` (FK → User.id), `name`, `address`, `telephoneNumber`, `email`, `createdAt`, `updatedAt`.

## 3. Data Persistence
*   **Storage Paradigm:** Relational (H2, file-mode) for this test application — self-contained, no external DB dependency required to verify the pipeline. Swappable to PostgreSQL/MySQL for a real production deployment via Spring Data JPA without code changes (only `application.properties` datasource config changes).
*   **Database Schema Guidelines:**
    - `users.email` — `UNIQUE NOT NULL` index (login lookups and username enforcement).
    - `contacts.owner_user_id` — `NOT NULL` foreign key with an index (per-user contact scoping and search performance, FR-11).
    - `contacts` — composite/secondary indexes on `(owner_user_id, name)` to support multi-field search (FR-10) at reasonable performance for a test app.
    - Cascade rule: deleting a `User` (FR-05) cascades to delete their owned `Contact` rows.

## 4. Services
*   **AuthService:** Handles registration (hash + persist new `User`), login (credential verification, JWT issuance), and self-service password change.
*   **AdminUserService:** Handles Admin-only user creation, user removal, and password-reset-generation for a target user. Enforces `ADMIN`-only access via `@PreAuthorize`.
*   **ContactService:** Handles add/edit/delete/search of `Contact` records, always scoped to the authenticated user's `ownerUserId`; search queries filter across `name`, `address`, `telephoneNumber`, `email` (FR-10).
*   **StartupAdminSeederService:** One-time bootstrap component that creates the initial Admin account if none exists (FR-02).

## 5. Sequencing of Operations
1. **Registration:** Client → `POST /auth/register` (email, password, address, phone) → `AuthController` validates payload → `AuthService` checks email uniqueness → hashes password → `UserRepository` persists → returns confirmation.
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
