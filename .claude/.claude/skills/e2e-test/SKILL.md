---
name: e2e-test
description: Executes end-to-end integration and frontend testing, applies risk-based prioritization, conducts functional/non-functional checks, and manages defects in Project Documents.
---
# Comprehensive Quality Assurance & E2E Testing Playbook

When this skill is triggered or `/e2e-test` is invoked following a deployment to the Test Environment by `@dev-ops`, execute the following multi-stage QA engineering process:

---

## Stage 1: Shift-Left Requirements Analysis & Risk Assessment
Before executing tests against the deployed build, analyze source documentation early to define testable boundaries:
1. **Source Audit:** Read `/Project Documents/Requirements.md` (BRD) and `/Project Documents/Architecture.md`.
2. **Risk-Based Prioritization:** Assign a testing priority level based on business impact:
   *   **P1 (Critical):** Core revenue/security paths (e.g., Iteration 1 User Registration, Login, Authentication, Password Reset). Requires 100% test pass rate.
   *   **P2 (High):** Primary business domain features and workflow logic.
   *   **P3 (Medium/Low):** Secondary UI components, administrative views, or cosmetic styling.

---

## Stage 2: Test Planning & Environment Verification
1. **Target Verification:** Parse the Test Environment URLs and build verification hashes provided by the `@dev-ops` hand-off log.
2. **Environment Health Check:** Perform a ping/health check against the live Test Environment endpoints to verify services are responsive before running test suites.
3. **Criteria Definition:** Establish explicit Pass/Fail thresholds based on functional acceptance criteria and non-functional performance baselines.

---

## Stage 3: Test Design & Suite Construction
Develop comprehensive automated test scripts (e.g., Playwright, Cypress, or integration test runners) and manual verification procedures covering three core testing pillars:

### 1. Functional Testing
*   **System & Integration Testing:** Verify backend API endpoints and frontend UI components interact seamlessly together.
*   **User Acceptance Testing (UAT):** Validate end-to-end user journeys against the acceptance criteria defined in `Requirements.md`.

### 2. Regression Testing
*   **Suite Execution:** Execute pre-existing test suites across previously merged iterations to ensure new code changes have not broken established features.

### 3. Non-Functional Testing
*   **Security Assessment:** Audit input forms and endpoints for common vulnerabilities (e.g., OWASP Top 10, unauthenticated API access, improper session handling).
*   **Performance & Load:** Verify application load speeds and HTTP response latencies meet non-functional requirements.
*   **Usability & Accessibility:** Check UI layout responsiveness, error message clarity, and basic accessibility standards across screen sizes.

---

## Stage 4: Test Execution (CI/CD Pipeline Integration)
1. Run the test suites against the deployed Test Environment.
2. Capture execution logs, HTTP response payloads, console outputs, and UI screenshot artifacts for any failed assertions.

---

## Stage 5: Defect Management & Tracking
If any test case fails or behaves unexpectedly:
1. Verify if the file `/Project Documents/DEFECTS.md` exists. If not, create it.
2. Log all identified bugs into `/Project Documents/DEFECTS.md` using this standardized format:

```markdown
### Defect ID: DEF-[Iteration]-[Number]
*   **Severity:** [Critical / Major / Minor]
*   **Impact Area:** [e.g., User Authentication / Service API / UI Layout]
*   **Description:** Clear explanation of the observed issue.
*   **Steps to Reproduce:**
    1. Step 1
    2. Step 2
*   **Expected Result:** [Behavior dictated by Requirements.md]
*   **Actual Result:** [Observed failing behavior]
*   **Status:** [OPEN / RE-TESTED / CLOSED]