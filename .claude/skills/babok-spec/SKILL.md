---
name: babok-spec
description: Applies BABOK standards to conduct a user interview and generate an industry-aligned Business Requirements Document (BRD) inside the Project Documents folder.
---
# BABOK Industry-Aligned Requirements Playbook

When this skill is triggered or `/babok-spec` is invoked, execute the following multi-phase process:

## Phase 1: Context Intake & Gap Analysis
1. Read the root `PROJECT_CHARTER.md` and `README.md` to establish baseline context (Project Name, Description, Scope).
2. Identify what granular technical and business details are missing to form a true enterprise-grade BRD.

## Phase 2: Interactive Prompt Phase (User Interview)
Halt autonomous execution. Interview the user to collect or refine the deep requirements criteria. To minimize cognitive load, present these queries clearly, pre-populating information you can already infer from the Project Charter for their confirmation:

1. **Executive Summary Elaborations:** Any specific timeline targets or budget constraints to note?
2. **Project Objectives:** What are the explicit SMART goals (Specific, Measurable, Actionable, Relevant, Time-bound) that prove business value?
3. **Stakeholder Mapping:** Who are the key users/personas impacted, and what is their level of involvement?
4. **Business Needs Statement:** What is the background of the current manual or legacy process driving this problem/opportunity?
5. **Functional Requirements:** What are the specific core features, user behaviors, and system actions needed (e.g., explicit user roles, workflows)?
6. **Non-Functional Requirements:** What are the exact expectations for data security, performance/speed latency, and usability standards?
7. **Assumptions & Constraints:** Are there technical limitations (e.g., language restrictions, third-party APIs) or baseline assumptions the team must follow?

## Phase 3: Directory & File Creation
1. Verify if a folder named `Project Documents` exists at the root of the project directory. If it does not exist, create it.
2. Generate a comprehensive requirements file named `Requirements.md` inside that folder path: `/Project Documents/Requirements.md`.

## Phase 4: BRD Markdown Template Structure
Format the `/Project Documents/Requirements.md` file using this exact enterprise outline:

```markdown
# Business Requirements Document (BRD)

## 1. Executive Summary
*   **Project Overview:** [High-level summary of the initiative]
*   **Purpose & Business Justification:** [Why this project matters]
*   **Timeline & Budget Targets:** [Key constraints and windows]

## 2. Project Objectives
[Clear, measurable SMART criteria goals tied directly to broader business value metrics]

## 3. Project Scope
*   **In-Scope Boundaries:** [Explicitly defined features and implementations]
*   **Out-of-Scope Boundaries:** [Explicit items excluded to aggressively mitigate scope creep]

## 4. Stakeholder Mapping
| Stakeholder / Persona | Role in Project | Level of Involvement / Impact |
| :--- | :--- | :--- |
| **Product Owner (User)**| Vision, Sign-off, Acceptance Gates | High / Ultimate Approver |
| [Persona Name] | [e.g., End User, Admin, Auditor] | [High / Medium / Low] |

## 5. Business Needs Statement
*   **Problem / Opportunity Statement:** [The core driving issue]
*   **Current Process & Context:** [Background of legacy systems or current workflow friction]

## 6. Functional Requirements
[Specific system capabilities, structural feature mechanics, data inputs, and system behaviors]
*   **FR-01:** [Requirement description]
*   **FR-02:** [Requirement description]

## 7. Non-Functional Requirements
*   **Data Security & Compliance:** [Encryption, authorization rules, privacy standards]
*   **Performance & Scalability:** [Speed latency expectations, load thresholds]
*   **Usability & Accessibility:** [UI design rules, responsiveness standards]

## 8. Assumptions & Constraints
*   **Technical / Resource Constraints:** [Technology stack limits, budget walls, api constraints]
*   **Baseline Assumptions:** [Core assumptions made by the project team]