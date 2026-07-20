---
name: project-charter
description: Initiates an interactive prompt sequence with the user to collect project information and generate a formal Project Charter markdown file.
---
# Project Charter Creation Playbook

When this skill is triggered or `/project-charter` is invoked, execute the following step-by-step sequence:

## Phase 1: Interactive Prompt Phase
Do not generate the charter immediately. Stop and interview the user by asking them for the following details. You can ask these questions one by one or as a clean list to minimize cognitive load:
1. **Project Name:** What is the official title of this application/initiative?
2. **Project Description:** What core problem does this software solve?
3. **Success Criteria:** What metrics or conditions define a successful delivery?
4. **High-Level Scope:** What features are explicitly In-Scope vs. Out-of-Scope?
5. **Milestones and Timeline:** What are the key target dates or target phases?

## Phase 2: Charter Generation
Once the user provides the answers, compile them into a file named `PROJECT_CHARTER.md` in the project root directory. Use the exact structural template below:

```markdown
# Project Charter: [Insert Project Name]

## 1. Project Description
[Insert detailed Project Description provided by user]

## 2. Success Criteria
[Insert specific, measurable success criteria provided by user]

## 3. High-Level Scope
[Insert In-Scope and Out-of-Scope details provided by user]

## 4. Milestones & Timeline
[Insert target milestones and timeline phases provided by user]

## 5. Roles & Responsibilities (SDLC AI Project Team)
The project execution matrix is defined below. The human user drives the vision while the automated agent pipeline executes the lifecycle:

*   **Product Owner (The User):** Ultimate stakeholder. Answers prompt phases, defines high-level vision, sets acceptance criteria, and signs off on deliverables.
*   **Project Manager Agent (`@project-manager`):** Interprets this charter, constructs milestone plans, breaks down features into tasks, and coordinates work delegation across sub-agents.
*   **Business Analyst Agent (`@business-analyst`):** Follows the BABOK framework to extract system requirements from the scope and creates the `REQUIREMENTS.md` file.
*   **Software Architect Agent (`@software-architect`):** Creates system architecture, patterns, component diagrams, and layout blueprints in `ARCHITECTURE.md` based on requirements.
*   **Software Developer Agent (`@software-developer`):** Implements clean code and unit tests following the architectural blueprints.
*   **Code Reviewer Agent (`@code-reviewer`):** Audits newly written code against architectural standards, security principles, and gives final production approval.
*   **QA Tester Agent (`@qa-tester`):** Generates and runs external end-to-end frontend tests to verify user-facing requirements match criteria.
*   **DevOps Agent (`@dev-ops`):** Manages environments, compiles production builds, containerizes components, and coordinates deployment steps.

## Phase 3: Hand-Off
After saving `PROJECT_CHARTER.md`, confirm the file creation to the user. Do not call `@dev-ops` directly or scaffold any codebase yet. Hand control over to the `@project-manager` for the Universal Document Review Gate on the Charter, followed by delegation to `@business-analyst` (`/babok-spec`) and `@software-architect` (`/architecture-design`). Codebase scaffolding via `@dev-ops` (`/pipeline-deploy` Workflow A) is only triggered after Requirements, Architecture, and the WBS are drafted and approved.