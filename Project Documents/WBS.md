# Work Breakdown Structure (WBS): demo-address-book

Vertical, security-first slice plan derived from `Requirements.md` (BRD) and `Architecture.md`. Iteration 1 delivers the full user/admin access flow (Milestone 1) before any contact-management business logic (Milestone 2) begins, per the Project Charter.

## Iteration 1 — Foundation & Security (Milestone 1)
Branch: `iteration-1-security`

| Task ID | Description | Maps To | Dependencies | Effort | Assigned Agent |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TSK-1.1 | Author `users` table schema/migration (unique email index) | Architecture §3 | WBS Approved | S | `@software-developer` |
| TSK-1.2 | Implement `User` `@Entity` + unit tests | FR-01, FR-02 | TSK-1.1 | S | `@software-developer` |
| TSK-1.3 | Implement `UserRepository` + `@DataJpaTest` suite | Architecture §4/§7 | TSK-1.2 | S | `@software-developer` |
| TSK-1.4 | Implement `AuthService` (register, login, self-service password change) + `StartupAdminSeederService`, with Mockito unit tests | FR-01, FR-02, FR-03 | TSK-1.3 | M | `@software-developer` |
| TSK-1.5 | Implement `AuthController`/`AccountController` (`/auth/register`, `/auth/login`, `/account/password`) + `@WebMvcTest` suite | FR-01, FR-02, FR-03 | TSK-1.4 | M | `@software-developer` |
| TSK-1.6 | Implement `AdminUserService`/`AdminUserController` (add user, remove user, reset password) + unit tests, `@PreAuthorize` RBAC | FR-04, FR-05, FR-06 | TSK-1.5 | M | `@software-developer` |
| TSK-1.7 | Frontend: registration/login/admin-management UI (HTML/JS/CSS) + JS unit tests | FR-01–FR-06 | TSK-1.6 | M | `@software-developer` |
| TSK-1.8 | Code review of full Iteration 1 slice (security, RBAC, coverage) | All Iteration 1 | TSK-1.7 | S | `@code-reviewer` |
| TSK-1.9 | Merge `iteration-1-security` → `main`, regression suite, deploy to Test Environment | Pipeline Workflow B | TSK-1.8 Approved | S | `@dev-ops` |
| TSK-1.10 | E2E test: registration, login, self-service password change, admin add/remove user, admin password reset | FR-01–FR-06 | TSK-1.9 | M | `@qa-tester` |
| TSK-1.11 | Production build & release — Milestone 1 | Pipeline Workflow C | TSK-1.10 PASSED | S | `@dev-ops` |

## Iteration 2 — Contact Management (Milestone 2)
Branch: `iteration-2-contacts`

| Task ID | Description | Maps To | Dependencies | Effort | Assigned Agent |
| :--- | :--- | :--- | :--- | :--- | :--- |
| TSK-2.1 | Author `contacts` table schema/migration (FK to `users`, search indexes) | Architecture §3 | TSK-1.11 | S | `@software-developer` |
| TSK-2.2 | Implement `Contact` `@Entity` + unit tests | FR-07–FR-11 | TSK-2.1 | S | `@software-developer` |
| TSK-2.3 | Implement `ContactRepository` (multi-field search query) + `@DataJpaTest` suite | FR-10 | TSK-2.2 | M | `@software-developer` |
| TSK-2.4 | Implement `ContactService` (CRUD + search, strict per-user ownership enforcement) + Mockito unit tests | FR-07–FR-11 | TSK-2.3 | M | `@software-developer` |
| TSK-2.5 | Implement `ContactController` + `@WebMvcTest` suite | FR-07–FR-11 | TSK-2.4 | M | `@software-developer` |
| TSK-2.6 | Frontend: contact list/add/edit/delete/search UI + JS unit tests | FR-07–FR-11 | TSK-2.5 | M | `@software-developer` |
| TSK-2.7 | Code review of full Iteration 2 slice (ownership checks, coverage) | All Iteration 2 | TSK-2.6 | S | `@code-reviewer` |
| TSK-2.8 | Merge `iteration-2-contacts` → `main`, regression suite, deploy to Test Environment | Pipeline Workflow B | TSK-2.7 Approved | S | `@dev-ops` |
| TSK-2.9 | E2E test: contact add/edit/delete/search, cross-user isolation regression | FR-07–FR-11 | TSK-2.8 | M | `@qa-tester` |
| TSK-2.10 | Production build & release — Milestone 2 | Pipeline Workflow C | TSK-2.9 PASSED | S | `@dev-ops` |

## Status Tracking
| Task ID | Status |
| :--- | :--- |
| TSK-1.1 – TSK-1.7 | `COMPLETE` — `@software-developer` implemented repositories, JWT/security infra, services, controllers, and React/TS frontend for Milestone 1, with unit tests at every layer |
| TSK-1.8 | `COMPLETE` — `@code-reviewer` found 2 blocking issues (user-ID disclosure via `UserNotFoundException`, missing CORS config), both remediated in commit `126506b` and re-reviewed: `[APPROVED FOR PRODUCTION]` |
| TSK-1.9 | `COMPLETE` — `iteration-1-security` merged (fast-forward) and pushed to `main`; full regression suite green (65 backend + 27 frontend tests); app deployed locally as Test Environment (`:8080`/`:5173`) |
| TSK-1.10 | `COMPLETE` — `@qa-tester` E2E pass on FR-01–FR-06 via live HTTP calls: 100% pass, 0 Critical/Major defects. One Minor non-gating defect logged (`DEF-1-01`, unused `GET /admin/users` returns 500) — see `Project Documents/DEFECTS.md` |
| TSK-1.11 | `COMPLETE` — production build for backend (`addressbook-1.0.0-SNAPSHOT.jar`) and frontend (`frontend/dist/`) succeeded; tagged `milestone-1-v1.0.0` locally (not yet pushed) |
| TSK-2.1 – TSK-2.10 | `NOT STARTED` — Milestone 1 shipped; Iteration 2 (Contact Management) can now begin |

Scaffolding (`/pipeline-deploy` Workflow A) is `COMPLETE`: project scaffolded at `C:\Users\Anthony Orlando\workspace\demo-address-book`, green build verified, commit `5e32bd0` pushed to `main` and `iteration-1-security` on GitHub.

Milestone 1 (Iteration 1 — Foundation & Security) is `COMPLETE` as of commit `1559322` on `main`, tagged `milestone-1-v1.0.0`.
