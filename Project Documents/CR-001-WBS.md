# Work Breakdown Structure: CR-001 — Revision for Address Specification

Scoped entirely to `CR-001-address-specification-revision.md`'s approved Requirements.md v1.1 / Architecture.md v1.1 delta. This is a dedicated CR-specific WBS — it is not merged into or appended to `WBS.md`, per `change-request/SKILL.md` Phase 4.

Branch: `cr-001-address-specification`

| Task ID | Description | Maps To | Dependencies | Effort | Assigned Agent | Status | GitHub Issue |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| CR1-TSK-1 | Update `users`/`contacts` schema: drop `address`, add `address_line1`, `address_line2`, `city`, `state`, `zip_code` columns (Hibernate-managed DDL — no migration script needed per Architecture.md §3) | Architecture §3 | CR-001 Approved | S | `@software-developer` | Complete | #1 |
| CR1-TSK-2 | Update `User` and `Contact` `@Entity` classes with the 5 new fields + unit tests | Requirements §7, Architecture §2 | CR1-TSK-1 | S | `@software-developer` | Complete | #2 |
| CR1-TSK-3 | Update `RegisterRequest`/`ContactRequest`/`ContactResponse` DTOs with the 5 new fields + validation annotations (USPS 2-letter state pattern, ZIP/ZIP+4 pattern) | Requirements §7/§8 (Data Validation & Standards NFR) | CR1-TSK-2 | S | `@software-developer` | Complete | #3 |
| CR1-TSK-4 | Update `AuthService`/`ContactService` to persist/return the new fields; update `ContactRepository` search query to match across all 5 address columns instead of one | FR-01, FR-07, FR-10 | CR1-TSK-3 | M | `@software-developer` | Complete | #4 |
| CR1-TSK-5 | Update `AuthController`/`ContactController` request handling + `@WebMvcTest` suites for the new fields | FR-01, FR-07 | CR1-TSK-4 | S | `@software-developer` | Complete | #5 |
| CR1-TSK-6 | Frontend: replace the single address input with the 5 structured fields on registration and contact add/edit/search forms + JS unit tests | FR-01, FR-07, FR-10 | CR1-TSK-5 | M | `@software-developer` | Complete | #6 |
| CR1-TSK-7 | Code review of the full CR-001 slice (schema delta correctness, validation rules, search query correctness, test coverage) | All CR-001 tasks | CR1-TSK-6 | S | `@code-reviewer` | Complete | #7 |
| CR1-TSK-8 | Merge `cr-001-address-specification` → `main`, regression suite, deploy to Test Environment | Pipeline Workflow B | CR1-TSK-7 Approved | S | `@dev-ops` | Complete | #8 |
| CR1-TSK-9 | E2E test: registration and contact add/edit/search with structured address, including USPS state-abbreviation and ZIP/ZIP+4 validation edge cases | FR-01, FR-07, FR-10 | CR1-TSK-8 | M | `@qa-tester` | Planned | #9 |
| CR1-TSK-10 | Production build & release — CR-001 | Pipeline Workflow C | CR1-TSK-9 PASSED | S | `@dev-ops` | Planned | #10 |

**Effort key:** S = small (~1 file/class), M = medium (~multi-file layer touching several classes).
