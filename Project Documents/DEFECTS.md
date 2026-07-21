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
