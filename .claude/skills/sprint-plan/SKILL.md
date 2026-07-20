---
name: sprint-plan
description: Coordinates the master SDLC pipeline, enforces document review gates, triggers DevOps scaffolding, delegates QA testing, routes production builds post-QA pass, and delivers milestone progress reports.
---
# Master SDLC Orchestration & Project Management Playbook

When this skill is triggered or `/sprint-plan` is invoked, execute and maintain the following strict project management framework:

## Phase 1: Universal Document Review & Commit Gate
For **every single core project document** generated across the lifecycle (`PROJECT_CHARTER.md`, `Requirements.md`, `Architecture.md`, and `WBS.md`), enforce an absolute manual review gate:
1. **Present and Halt:** Display the finalized draft of the current document to the user and halt autonomous execution. Ask explicitly: *"Please review this document. Do you provide formal approval to commit this asset to the main branch?"*
2. **DevOps Commit Invocation:** The moment the user provides approval, immediately route execution to the `@dev-ops` agent with instructions to commit that specific document directly to the `main` branch before moving to the next SDLC stage.

## Phase 2: Security-First Vertical WBS Strategy
When constructing the Work Breakdown Structure, analyze `/Project Documents/Requirements.md` (BRD) and `/Project Documents/Architecture.md` to draft a vertical, slice-by-slice implementation map:
*   **Iteration 1 (Foundation & Security):** Must deliver an end-to-end user access flow (registration, login authentication, password reset) with a functional UI landing page or service endpoint before any business domain logic is scheduled.
*   **Iterations 2 through N (Vertical Business Features):** Schedule feature slices moving vertically from the presentation layer down through the service layer, repository layer, and database schema.
*   **Task Properties:** Every task must map a Task ID, Action-Oriented Description, Dependencies, Effort Estimate, and an Assigned Agent (`@software-developer`, `@code-reviewer`, `@qa-tester`, `@dev-ops`).

## Phase 3: The Approved WBS Scaffolding Trigger
The orchestration timeline for setting up the workspace directory must follow this exact sequence:
1. Deliver the WBS draft to the user via the Phase 1 Review Gate.
2. Once the user explicitly approves the WBS, hand control to the `@dev-ops` agent.
3. This approved status serves as the direct trigger for `@dev-ops` to execute structural folder scaffolding via `/pipeline-deploy` (Workflow A).

## Phase 4: Feature Completion, QA Testing, & Production Build Loop
When notified by `@dev-ops` that a feature build has successfully passed unit tests, code review, merged to `main`, and deployed to the Test Environment:

1. **QA Delegation:** Hand control over to the QA Tester Agent (`@qa-tester`) to run `/e2e-test` against the Test Environment.
2. **Defect Remediation Path (If QA Fails):** If `@qa-tester` reports critical defects logged in `/Project Documents/DEFECTS.md`, route the defect log back to `@software-developer` for patching and re-entry into the DevOps CI/CD loop.
3. **Production Build Hand-off (If QA Passes):** When `@qa-tester` issues a formal `PASSED` report with zero blocking defects:
   *   **Delegate to DevOps:** Immediately instruct the `@dev-ops` agent to execute **Workflow C: Production Build & Release Pipeline** in `/pipeline-deploy`.
   *   **Production Confirmation:** Wait for `@dev-ops` to confirm production artifact compilation, Git version tagging, and live Production Environment deployment.
4. **Task Completion:** Upon receiving the production release confirmation from `@dev-ops`, officially mark the task as `COMPLETE` in `/Project Documents/WBS.md`.

## Phase 5: Milestone Progress Reporting Matrix
At the conclusion of every major milestone or iteration boundary defined in your WBS, halt normal workflows to generate and output a comprehensive progress report across all 5 SDLC phases:

```text
======================================================================
                 MILESTONE PROGRESS REPORT: [Milestone Name]
======================================================================
Current Project State: [Active / Blocked / Ready for Sign-off]

[ ] PHASE 1: REQUIREMENTS (Business Analyst)
    - Status: [100% Complete / BRD Approved]

[ ] PHASE 2: ARCHITECTURE & DESIGN (Software Architect)
    - Status: [100% Complete / Spring Monolith Architecture Locked]

[ ] PHASE 3: DEVELOPMENT & SOURCE CODE (Software Developer)
    - Status: [e.g., TSK-1.1 Complete / Unit Tests Passing]

[ ] PHASE 4: QUALITY ASSURANCE & TESTING (QA Tester)
    - Status: [PASSED / Zero Open Critical Defects in DEFECTS.md]

[ ] PHASE 5: DEVOPS & INFRASTRUCTURE (DevOps Engineer)
    - Status: [PRODUCTION LIVE / Release Tag Created / Main Synced]
    - Production Release Details: [Git Tag / Production Build Artifact Hash]
======================================================================