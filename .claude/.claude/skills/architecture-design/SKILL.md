---
name: architecture-design
description: Evaluates the project charter and BRD to design a multi-layered Spring Framework monolithic architecture blueprint inside the Project Documents folder.
---
# Monolithic System Architecture Playbook

When this skill is triggered or `/architecture-design` is invoked, execute the following multi-phase technical design workflow:

## Phase 1: Input & Context Consumption
1. Navigate to the `/Project Documents/` folder at the project root.
2. Fully parse and extract context from both `PROJECT_CHARTER.md` and `Requirements.md` (BRD).
3. Do not assume or guess technical stacks; you are strictly bound to the high-level scope and functional requirements defined within these source documents.

## Phase 2: Architectural Standard Approach Template
Develop the technical specifications following this exact 7-part engineering blueprint:

### 1. Security and User Access
*   **Authentication & Authorization Strategy:** [Define how users/systems authenticate, session mechanics, token handling, or Spring Security configurations]
*   **Role-Based Access Control (RBAC):** [Map out specific security access paths/rules per persona identified in Stakeholder Mapping]

### 2. Core Business Entities
[Define the foundational business models, data objects, and domain schemas required to fulfill the functional requirements]

### 3. Data Persistence
*   **Storage Paradigm:** [Define the chosen database engine, e.g., relational, NoSQL, or in-memory targets]
*   **Database Schema Guidelines:** [Outline constraints, indexes, keys, and data-integrity rules mapping back to the Business Needs]

### 4. Services
[Detail the core orchestrations, transactional boundary logic, processing algorithms, and external API integrations]

### 5. Sequencing of Operations
[Provide step-by-step linear call flows or text-based sequence descriptions showing how data flows through the layers during major functional routines]

### 6. System Topology
*   **Package Structure:** [Draft the high-level Java package layout]
*   **Domain Boundaries:** [Isolate domain and module grouping definitions]
*   **Entity Mapping:** [Define the structural components linking tables to application data]

### 7. Spring Framework Design: Monolithic Multi-Layered Pattern
The overall system design must strictly adhere to the following vertical multi-layered N-Tier component architecture. Data moves sequentially down and back up through these structured components:
*   **API / Presentation Layer:** Consists of `@RestController` or `@Controller` beans that receive incoming HTTP requests, map JSON data payload schemas, and apply initial input data validations.
*   **Service Layer:** Houses the core business algorithms and workflow rules decorated with `@Service` and `@Transactional` tags to govern data mutation boundaries.
*   **Data Access / Repository Layer:** Leverages Spring Data JPA or MyBatis interfaces annotated with `@Repository` to interact cleanly with persistent storage databases.
*   **Domain / Entity Layer:** Comprises plain Java objects (POJOs) mapping business definitions or exact relational database schema properties directly via `@Entity` mapping rules.

## Phase 3: Final Execution Step
1. **Create the Architecture.md file in the project documentation folder:** Compile all 7 parts of the engineering blueprint developed in Phase 2 and save the final document directly to the file path `/Project Documents/Architecture.md`.
2. **Hand it off to the project manager:** Output a confirmation log to the user summarizing the system topology choices, exit the architect subagent routine, and explicitly yield operational control back to the `@project-manager` for pipeline sign-off before any developer coding pipelines can begin.