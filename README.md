# demo-address-book

An online application that holds names, addresses, telephone numbers, and email addresses for a user's contacts. Built as a test application to verify the end-to-end SDLC agent pipeline.

## Project Overview

**demo-address-book** is a full-stack web application featuring:
- User registration and authentication (email-based login)
- Contact management (CRUD operations)
- Role-based access control (USER and ADMIN roles)
- Admin account auto-generation at startup
- Secure password management and self-service password change

## Folder Layout

```
demo-address-book/
├── backend/                  # Spring Boot REST API (Java 17, Maven)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/         # Application code
│   │   │   └── resources/    # Configuration files
│   │   └── test/             # Unit tests
│   └── pom.xml               # Maven configuration
├── frontend/                 # Vite + React + TypeScript
│   ├── src/                  # React components & pages
│   ├── package.json          # npm dependencies
│   └── dist/                 # Built production artifacts
├── Project Documents/        # SDLC documentation
│   ├── PROJECT_CHARTER.md    # Project vision & scope
│   ├── Requirements.md       # Business requirements (FR/NFR)
│   ├── Architecture.md       # System design & entity models
│   └── WBS.md                # Work breakdown structure
└── .claude/                  # Multi-agent team configuration
```

## Active Development Branch

**Current Branch:** `iteration-1-security`

This branch focuses on **Iteration 1: Foundation & Security**, which includes:
- User registration and login (JWT-based authentication)
- Admin account auto-generation and password reset
- Role-based access control (RBAC) enforcement

Contact management features are planned for **Iteration 2**.

## Local Development Setup

### Prerequisites
- **Java 17+** (e.g., OpenJDK Corretto 17)
- **Maven 3.9+**
- **Node.js 18+** and **npm 9+**

### Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

The API server will start on `http://localhost:8080`.

**Database:** H2 (file-based, self-contained)
- Location: `./data/addressbook.mv.db`
- Admin console: `http://localhost:8080/h2-console` (development only)

### Frontend (React + Vite)

```bash
cd frontend
npm run dev
```

The development server will start on `http://localhost:5173`.

For production build:
```bash
npm run build
npm run preview  # Preview production build locally
```

## Architecture Highlights

### Technology Stack
- **Backend:** Spring Boot 3.1, Spring Security, Spring Data JPA, JWT (jjwt)
- **Frontend:** React 18, TypeScript, Vite
- **Database:** H2 (relational, file-mode)
- **Build Tools:** Maven, npm

### API Endpoints (Phase 1)

**Authentication:**
- `POST /auth/register` — User registration
- `POST /auth/login` — User login (returns JWT)

**Admin User Management:**
- `POST /admin/users` — Create user (admin only)
- `DELETE /admin/users/{id}` — Remove user (admin only)
- `POST /admin/users/{id}/reset-password` — Reset password (admin only)

**Account Management:**
- `PUT /account/password` — Self-service password change

**Contact Management (Phase 2):**
- Contact CRUD endpoints planned for Iteration 2

### Security Model

- **Authentication:** Stateless JWT bearer tokens
- **Authorization:** Role-based access control (USER, ADMIN)
- **Password Storage:** BCrypt hashing via Spring Security `PasswordEncoder`
- **Initial Admin:** Auto-generated at startup with random password (logged for operator retrieval)

## Development Workflow

1. **Feature Development:** Create a feature branch from `iteration-1-security`
2. **Build & Test:** `mvn clean install -q` (backend) and `npm run build --silent` (frontend)
3. **Code Review:** Merge to `main` only after approval
4. **Deployment:** Push to GitHub and trigger deployment pipeline

## Documentation

Refer to the documents in `Project Documents/` for:
- **PROJECT_CHARTER.md:** Vision, scope, milestones, and SDLC team roles
- **Requirements.md:** Detailed functional and non-functional requirements
- **Architecture.md:** System design, entity models, service layer design
- **WBS.md:** Work breakdown structure and iteration planning

## Troubleshooting

### Backend Build Issues
If `mvn clean install` fails:
1. Verify `JAVA_HOME` points to Java 17+
2. Clear Maven cache: `mvn clean`
3. Check `pom.xml` for dependency resolution errors

### Frontend Build Issues
If `npm run build` fails:
1. Delete `node_modules/` and `package-lock.json`
2. Run `npm install` again
3. Verify Node.js version: `node -v` (should be 18+)

## Project Status

- **Phase:** Iteration 1 — Foundation & Security
- **Branch:** `iteration-1-security`
- **Upcoming:** User registration, login, and admin functionality
- **Next Phase:** Iteration 2 — Contact Management

---

*Built with the multi-agent SDLC pipeline. For questions or updates, refer to the project coordinator or check the active branch documentation.*
