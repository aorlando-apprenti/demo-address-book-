---
name: feature-dev
description: Implements functional application slices layer-by-layer, authors backend JUnit and frontend JS unit tests aiming for 100% code coverage, and triggers DevOps build checks.
---
# Layered Vertical Feature Development Playbook

When this skill is triggered or `/feature-dev` is invoked to build an iteration task, execute the following multi-phase development workflow:

## Phase 1: Documentation Compliance Audit
Before writing a single line of code, parse and cross-reference **all** source records located in the `/Project Documents/` folder:
1. Read `PROJECT_CHARTER.md` for high-level alignment.
2. Read `Requirements.md` (BRD) to verify the exact functional and non-functional acceptance criteria.
3. Read `Architecture.md` to align with package namespaces, security parameters, and data models.
4. Read `WBS.md` to isolate the current active task boundary.

## Phase 2: Software Coding & Development Standards References
You must write code that strictly complies with the established architectural paradigms and corporate coding standards. Use the following hyperlinked references to evaluate syntax, patterns, and style guardrails before implementation:

*   **Java & Spring Boot Standards:** [Insert Java/Spring Style Guide Hyperlink Here](https://example.com/java-standards)
*   **Frontend & JavaScript/TypeScript Standards:** [Insert Frontend/NPM Style Guide Hyperlink Here](https://example.com/frontend-standards)
*   **Secure Coding Frameworks (OWASP):** [Insert Secure Coding Guidelines Hyperlink Here](https://example.com/security-standards)
*   **Database & SQL Migration Standards:** [Insert Database Best Practices Hyperlink Here](https://example.com/database-standards)

## Phase 3: Testing Strategy & 100% Code Coverage Mandate
*   **Goal:** Maintain a **100% Code Coverage target** across all application modules (`/backend` and `/frontend`).
*   **Backend Testing Stack:** Leverage JUnit 5, Mockito, and Spring Boot Test (`@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest`).
*   **Frontend Testing Stack:** Leverage JavaScript/TypeScript unit testing tools (e.g., Jest, Vitest, or React/DOM Testing Library).
*   **Rule:** Every class, service method, repository query, controller endpoint, and frontend UI logic script must have a corresponding unit test file.

## Phase 4: Standard Approach — Vertical Backend-First Sequencing
You must implement each functional component slice vertically, starting with the baseline data layers and finishing with the user interface presentation. For **each** vertical feature (beginning with Iteration 1: User Authentication), execute your coding steps and their corresponding unit tests in this exact order:

### [Backend Pipeline]
1.  **Data Storage:** Author the required database migration scripts or schema DDL. Write database validation scripts to verify constraints.
2.  **Model:** Write the plain Java entities (`@Entity`). Create matching unit tests ensuring getters, setters, constructors, and mapping constraints function accurately.
3.  **Repositories:** Create the `@Repository` interfaces. Author `@DataJpaTest` JUnit suites to verify custom database queries, finders, and persistence rules.
4.  **Services:** Write the business logic algorithms in `@Service` beans. Create corresponding JUnit tests using Mockito to mock repository dependencies and test 100% of branch logic and edge cases.
5.  **Controllers:** Implement `@RestController` entry points. Create corresponding `@WebMvcTest` JUnit suites using `MockMvc` to validate HTTP endpoints, status codes, JSON request/response payloads, and input validation failures.

### [Frontend Pipeline]
6.  **HTML Layout:** Scaffold structural markup and UI component templates.
7.  **JavaScript Functionality:** Write client-side logic, API fetch calls, state routines, and event handlers. Create matching JavaScript unit tests (using Jest/Vitest) to mock API calls and assert correct state changes and DOM updates.
8.  **CSS Style:** Apply styling rules and responsive UI aesthetics. Ensure layout rendering passes frontend snapshot/component unit checks.

## Phase 5: Component Handoff & Red-Green Verification Loop
Do not build multiple layers back-to-back without validation. **Each time a single functional component block + its unit test (Steps 1 through 8 above) is written or updated, execute this quality gate:**
1. Halt internal code generation.
2. Formally hand operational control over to the DevOps Agent (`@dev-ops`).
3. Direct `@dev-ops` to run the `/pipeline-deploy` skill to initiate the red-green build verification process (`mvn test` for Java backend and `npm test` for JS frontend).
4. **Resumption Rule:** Wait for `@dev-ops` to confirm a green build with 100% test pass rates. If any unit test fails or compilation breaks, fix the code/test, and resubmit to `@dev-ops` until a stable green environment is achieved.