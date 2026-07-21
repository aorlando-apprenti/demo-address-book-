# Defect Log — demo-address-book

## Iteration 1 (Milestone 1 — Foundation & Security), TSK-1.10 E2E Gate

### Defect ID: DEF-1-01
*   **Severity:** Minor
*   **Impact Area:** Admin User Administration API (`GET /admin/users`)
*   **Description:** `GET /admin/users` returns HTTP 500 ("An unexpected error occurred") for every caller — ADMIN role, USER role, and this was also confirmed with a valid ADMIN JWT. This endpoint is not called by the shipped frontend (`frontend/src/api/client.ts` only implements `createUser`/`removeUser`/`resetPassword`, no list-users call) and is not a documented endpoint in Architecture.md §"Admin" flows, nor a stated FR-04/05/06 acceptance criterion (add/remove/reset by known ID all work correctly — see PASSED results below). Does not block any FR-01–FR-06 acceptance criterion or the frontend admin workflow, so it is non-gating for Milestone 1, but should be fixed or removed before it is relied upon.
*   **Steps to Reproduce:**
    1. `POST /auth/login` as seeded admin (`admin@addressbook.local`) → capture JWT.
    2. `GET /admin/users` with `Authorization: Bearer <admin JWT>`.
    3. Repeat with a non-admin (USER role) JWT.
*   **Expected Result:** 200 with a JSON list of users (for ADMIN), or 403 (for USER) if this route is intentionally admin-only — in either case, no 500.
*   **Actual Result:** `HTTP 500 {"timestamp":"2026-07-21T09:21:11.9173957","status":500,"message":"An unexpected error occurred","fieldErrors":null}` for both ADMIN and USER tokens.
*   **Status:** OPEN

---

## Test Execution Summary — TSK-1.10 (2026-07-21)

Environment: backend http://localhost:8080 (health check 401 on unauthenticated `/actuator/health` — service responsive), frontend http://localhost:5173 (200 OK).

| FR | Test | Result | Evidence |
| :--- | :--- | :--- | :--- |
| FR-01 | `POST /auth/register` new user (email, password, address, telephoneNumber) | **PASS** | 201, `{"id":2,"email":"qa.testuser1@example.com",...,"role":"USER"}` |
| FR-02 | Login as seeded admin | **PASS** | 200, JWT issued, `"role":"ADMIN"` |
| FR-03 | `PUT /account/password` self-service change (old→new) as new user | **PASS** | 200 `{"message":"Password updated successfully."}`; old password login → 401; new password login → 200 |
| FR-04 | `POST /admin/users` admin creates user, returns temp password; new user logs in with temp password | **PASS** | 201 `{"user":{...,"id":3},"temporaryPassword":"erF2yakrn6Sj"}`; login with temp password → 200 |
| FR-06 | `POST /admin/users/{id}/reset-password` | **PASS** | 200 `{"userId":3,...,"newPassword":"TW28a9E$FgM3"}`; old temp password login → 401; new password login → 200 |
| FR-05 | `DELETE /admin/users/{id}` | **PASS** | 200 `{"message":"User removed successfully."}`; removed user login → 401 |
| Security (RBAC edge case) | Unauthenticated `GET /admin/users` and `GET /account` | **PASS** | Both → 401 (not 200) |
| Security (RBAC edge case) | Non-admin JWT on `POST /admin/users` | **PASS** | 403 `{"message":"Access is denied"}` |
| Security (RBAC edge case) | Non-admin JWT on `GET /admin/users` | **FAIL (see DEF-1-01)** | 500 instead of 403 — non-gating per above, endpoint unused/undocumented |

**FR-01 through FR-06: 6/6 PASS (100%), 0 Critical / 0 Major defects.** DEF-1-01 is Minor and non-gating (unused/undocumented endpoint, no FR or frontend dependency).

---

## Iteration 2 (Milestone 2 — Contact Management), TSK-2.9 E2E Gate

### Defect ID: DEF-2-01
*   **Severity:** Minor
*   **Impact Area:** Contact Management API (`GET /contacts/{id}`)
*   **Description:** `ContactController` (`backend/src/main/java/com/apprenticareers/addressbook/controller/ContactController.java`) has no `@GetMapping("/{id}")` handler — only `GET /contacts` (list), `GET /contacts/search`, `POST /contacts`, `PUT /contacts/{id}`, and `DELETE /contacts/{id}` are mapped, matching Requirements.md FR-07–FR-11 exactly, and the shipped frontend (`frontend/src/api/client.ts`) never calls a single-contact GET. However, hitting the unmapped route `GET /contacts/{id}` returns HTTP 500 ("An unexpected error occurred") instead of a clean 404, for every caller regardless of role or ownership (confirmed identical for the owning user, a non-owning user, and ADMIN). This is a non-gating API-hygiene issue, not a security/isolation leak — the response is identical and content-free regardless of who calls it or whether the target contact exists/belongs to them, so it does not violate FR-11.
*   **Steps to Reproduce:**
    1. Log in as any user with `Authorization: Bearer <JWT>`, create a contact via `POST /contacts`, note its `id`.
    2. `GET /contacts/{id}` (single-resource fetch) with that same user's JWT.
*   **Expected Result:** Either a supported 200 response with the contact, or a proper 404 if single-GET is intentionally unimplemented — not a 500.
*   **Actual Result:** `HTTP 500 {"timestamp":"2026-07-21T13:02:17.7096108","status":500,"message":"An unexpected error occurred","fieldErrors":null}`, reproduced identically for the owner, a different non-owning user, and the seeded ADMIN account.
*   **Status:** OPEN

---

## Test Execution Summary — TSK-2.9 (2026-07-21)

Environment: backend http://localhost:8080 (health check 401 on unauthenticated `/actuator/health` — service responsive), frontend http://localhost:5173 (200 OK). Two fresh test users registered (User A `qa.userA.m2@example.com`, User B `qa.userB.m2@example.com`); seeded ADMIN (`admin@addressbook.local`) used for RBAC-override check.

| FR | Test | Result | Evidence |
| :--- | :--- | :--- | :--- |
| FR-07 | `POST /contacts` — User A adds 3 contacts (distinct name/address/telephone/email) | **PASS** | 3× 201, ids 1–3 returned with full field echo |
| FR-08 | `PUT /contacts/{id}` — User A edits contact id 2 (name/address/phone/email all changed) | **PASS** | 200 with updated fields; re-verified via `GET /contacts` list — new values persisted, `updatedAt` advanced past `createdAt` |
| FR-10 | `GET /contacts/search?query=` — search by name, address, telephone, email (one case each) | **PASS** | Each query returned exactly the one matching contact (`Alice`→id1, `Pine`→id3, `444-555-7777`→id2, `carol.clark`→id3) |
| FR-10 | `GET /contacts/search?query=` blank value, and query param omitted entirely | **PASS** | Both → 200 with all 3 of User A's contacts (blank/absent query = return-all behavior) |
| FR-09 | `DELETE /contacts/{id}` — User A deletes contact id 1 | **PASS** | 200 `{"message":"Contact deleted successfully."}`; subsequent `GET /contacts` list no longer contains id 1 |
| FR-11 (critical) | User B: `GET /contacts` (list) and `GET /contacts/search?query=` blank, and `query=Carol` (User A's contact name) | **PASS** | List → `[]`; blank search → `[]`; search for User A's contact name → `[]` — zero leakage of User A's data into User B's own endpoints |
| FR-11 (critical) | User B: `PUT /contacts/2` and `DELETE /contacts/3` directly against User A's owned contact IDs | **PASS** | Both → 404 `{"message":"Resource not found",...}` — no 200, no 403-with-details, no existence disclosure |
| Admin RBAC override (critical, Architecture.md §RBAC) | Seeded ADMIN: `PUT /contacts/2` and `DELETE /contacts/3` directly against User A's owned contact IDs | **PASS** | Both → 404, byte-identical shape to the non-owning-user case — Admin has zero special access to contact data, exactly per Architecture.md RBAC table |
| Admin RBAC override | Seeded ADMIN: `GET /contacts` (list) and `GET /contacts/search?query=Carol` | **PASS** | Both → `[]` — Admin's own contact scope is empty and never surfaces other users' data |
| API hygiene (non-gating) | `GET /contacts/{id}` unmapped single-resource route, tested as owner, non-owner, and ADMIN | **FAIL (see DEF-2-01)** | 500 instead of 404 for all three callers — identical/content-free response regardless of caller, so not an FR-11 leak; non-gating |
| Regression (Milestone 1 spot-check) | Admin login (`admin@addressbook.local`) | **PASS** | 200, JWT issued, `"role":"ADMIN"` |
| Regression (Milestone 1 spot-check) | Self-service `PUT /account/password` (old→new) | **PASS** | 200 `{"message":"Password updated successfully."}`; old password login → 401; new password login → 200 |
| Regression (Milestone 1 spot-check) | Admin `POST /admin/users` add-user flow | **PASS** | 201 `{"user":{...,"id":4},"temporaryPassword":"AgV7%ZQ%XK98"}` |

**FR-07, FR-08, FR-09, FR-10: 4/4 PASS (100%). FR-11 (cross-user isolation) and Admin RBAC-override: 100% PASS, 0 leakage across all sub-cases tested — the single most important check for this iteration.** DEF-2-01 is Minor and non-gating (unimplemented/unused route returning 500 instead of 404; confirmed not a security/ownership leak since behavior is identical for owner, non-owner, and admin). Milestone 1 regression spot-check: 3/3 PASS, no regressions detected post-merge.

**0 Critical / 0 Major defects. Milestone 2 (FR-07–FR-11) is cleared to proceed to TSK-2.10 (production release).**
