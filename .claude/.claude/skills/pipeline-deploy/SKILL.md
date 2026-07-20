---
name: pipeline-deploy
description: Handles repository initialization, folder scaffolding, Maven/Spring & NPM/Node codebase generation, Git branching, CI/CD execution, code review gating, main branch merges, regression testing, test environment deployments, production builds, and PM handoffs.
---
# Environment Scaffolding, CI/CD & Deployment Playbook

When this skill is triggered, execute the corresponding workflow based on the pipeline phase:
*   **WORKFLOW A:** Initial Workspace Setup (Post-WBS Approval)
*   **WORKFLOW B:** Post-Feature Completion Integration & Test Deployment (Post-Dev Completion)
*   **WORKFLOW C:** Production Build & Release Pipeline (Post-QA Pass)

---

## WORKFLOW A: Initial Project Scaffolding & Environment Setup
*(Executed during project initialization post-WBS approval by the user)*

### Phase 1: Interactive Location Gathering
Halt execution and prompt the user for the structural infrastructure inputs:
1. **Parent Working Directory:** "Please paste the full absolute path to the parent directory where the project folder should be created (e.g., `C:\Projects` or `/Users/username/projects`)."
2. **GitHub Repository URL:** "Please provide the full HTTPS or SSH Git clone link for your remote GitHub repository."

### Phase 2: Folder Creation & Asset Migration
Once inputs are provided:
1. **Directory Naming:** Extract the Project Name from `PROJECT_CHARTER.md` (or `./Project Documents/PROJECT_CHARTER.md`) and sanitize it into a kebab-case folder name (e.g., `My App Name` $\rightarrow$ `my-app-name`).
2. **Target Path Construction:** Construct the full target path: `<Parent Working Directory>/<sanitized-project-name>`.
3. **Directory Creation:** Create the project root folder and an isolated document subfolder using shell commands:
   - Target Root: `<Parent Working Directory>/<sanitized-project-name>`
   - Document Subfolder: `<Parent Working Directory>/<sanitized-project-name>/Project Documents`
4. **Asset Migration:** Move `PROJECT_CHARTER.md`, `Requirements.md`, `Architecture.md`, and `WBS.md` from the current execution workspace into `<Parent Working Directory>/<sanitized-project-name>/Project Documents/`.
5. **Runtime Preservation:** Copy the hidden `.claude/` team configuration directory into `<Parent Working Directory>/<sanitized-project-name>/.claude/` to preserve the multi-agent runtime.
6. **Execution Shift:** Change active working context to `<Parent Working Directory>/<sanitized-project-name>`.

### Phase 3: Git Initialization & Branching Strategy
Establish a clean version-control lifecycle inside the new project root:
1. Run `git init`.
2. Link the remote host: `git remote add origin [User's GitHub URL]`.
3. Set the default primary tracking branch to `main`: `git branch -M main`.
4. **Iteration Branching:** Create and checkout a dedicated feature branch for the first vertical slice: `git checkout -b iteration-1-security`.

### Phase 4: Monolithic Codebase Scaffolding (Zero-Admin / User-Space)
*Rule: NEVER use OS package managers (winget, choco, apt). Use direct file generation, Spring Initializr API, or NPM CLI in user space.*

1. **Backend Project (`/backend`): Java & Spring Boot**
   - **Method A (Spring Initializr API):** Run `curl -s https://start.spring.io/starter.zip -d dependencies=web,security,data-jpa,test -d javaVersion=17 -d type=maven-project -o backend.zip`, unzip into `/backend`, and remove the zip.
   - **Method B (Direct Generation):** If offline or API fails, directly write `backend/pom.xml` with required starter dependencies (`spring-boot-starter-web`, `spring-boot-starter-security`, `spring-boot-starter-data-jpa`, `spring-boot-starter-test`) and create the primary `@SpringBootApplication` Java class file.
   - All Maven dependencies will download to `%USERPROFILE%\.m2` (no admin rights required).

2. **Frontend Project (`/frontend`): React & NPM**
   - Execute user-space NPM scaffolding in the project root: `npm create vite@latest frontend -- --template react-ts` (or `react`).
   - Navigate to `/frontend` and run `npm install --silent` to install dependencies locally in `node_modules` (no admin rights required).

### Phase 5: Quiet Green Build Verification Gate
Perform a local execution run to prove scaffolding stability:
1. **Backend Verification:** Navigate to `/backend` and execute `mvn clean install -q`.
2. **Frontend Verification:** Navigate to `/frontend`, run `npm install --silent`, and execute `npm run build --silent`.
3. **Recovery Rule:** If either environment fails, report ONLY the specific error stack trace (last 15-20 lines) and fix immediately before proceeding.

### Phase 6: README Generation
Generate a professional `README.md` file in the project root detailing project description, folder layout (`/backend`, `/frontend`, `/Project Documents/`), local developer onboarding commands, and active Git branch details (`iteration-1-security`).

### Phase 7: Initial Workspace Tracking Commit
1. Stage all files: `git add .`
2. Commit baseline: `git commit -m "chore: initial project scaffolding, established iteration-1-security branch, verified green build pipelines"`
3. Push `main` and active feature branch upstream:
   - `git push -u origin main`
   - `git push -u origin iteration-1-security`
4. Output compact hand-off to `@project-manager`.

---

## WORKFLOW B: Post-Feature Completion Integration & Test Deployment
*(Executed whenever `@software-developer` completes a feature slice or iteration task)*

1. **Quiet Unit Test Verification:** Run `mvn test -q -Dstyle.color=never` in `/backend` and `npm test -- --silent --watchAll=false` in `/frontend`.
2. **Code Review Gate:** Hand control to `@code-reviewer` via `/pr-review` and await `[APPROVED FOR PRODUCTION]`.
3. **Merge to Main:** Switch to `main`, pull latest (`git pull origin main`), and merge `iteration-[N]-[feature-name]`.
4. **Regression Build:** Run unit test suites on `main` to confirm zero regression errors.
5. **Test Environment Deployment:** Package compiled artifacts and deploy to the Test Environment.
6. **Hand-off to PM:** Output deployment log and return control to `@project-manager` for QA delegation (`/e2e-test`).

---

## WORKFLOW C: Production Build & Release Pipeline
*(Triggered by `@project-manager` once QA testing yields a PASSED status)*

1. **Production Artifact Compilation:** Compile backend binaries (`mvn clean package -q -Pprod`) and build minified frontend assets (`npm run build --silent`).
2. **Version Tagging & Git Release:** Switch to `main`, create annotated tag (`git tag -a v[X.Y.Z] -m "Production Release: Completed [Iteration Name]"`), and push upstream (`git push origin --tags`).
3. **Production Deployment:** Deploy compiled production artifacts to the Production Environment and perform health-check verification.
4. **Hand-off to PM:** Output release summary and yield control to `@project-manager` for task completion sign-off in `WBS.md`.