# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**HIM (Hospital Information Management)** — a full-stack hospital/clinic management system.

- **Backend**: Spring Boot 3.2.4 REST API (Java 21)
- **Frontend**: JavaFX 22 desktop application (Java 21, modular with module-info.java)
- **Database**: MySQL (JPA/Hibernate)
- **Build**: Maven for both modules (separate pom.xml files)

## Build & Run Commands

### Backend (`backend/`)

```bash
# Build (tests are skipped via <maven.test.skip>true</maven.test.skip>)
cd backend && mvn clean compile

# Run
cd backend && mvn spring-boot:run

# Package as JAR
cd backend && mvn clean package

# Run the packaged JAR (after packaging)
cd backend && java -jar target/javaFx-api-0.0.1-SNAPSHOT.jar
```

### Frontend (`frontend/`)

```bash
# Build (tests are skipped via <maven.test.skip>true</maven.test.skip>)
cd frontend && mvn clean compile

# Run the JavaFX application via Maven
cd frontend && mvn javafx:run

# Package as a fat JAR
cd frontend && mvn clean package -DskipTests

# Package as a native installer
cd frontend && mvn clean package jpackage:jpackage
```

### Both modules

```bash
# Build both backend and frontend
cd backend && mvn clean compile && cd ../frontend && mvn clean compile
```

## Server

- Backend runs on **http://localhost:8080**
- API base path: `/api` prefix added automatically via `WebConfig.configurePathMatch()` for all controllers in `com.example.controller` package
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI docs: http://localhost:8080/v3/api-docs

## Database

- Default database: `his` on MySQL port `3307`
- Default credentials: `root` / `123456`
- JPA DDL auto: `update` (Hibernate auto-creates/updates tables)
- Default admin account: `admin` / `admin123` (seeded by `DataInitializer`)

## Architecture

### Backend (Spring Boot) — Package layout under `com.example`

```
controller/     → REST controllers (mapped via @RequestMapping under /api/auth, /api/album, etc.)
service/        → Service interfaces
service/impl/   → Service implementations
repository/     → Spring Data JPA repositories
entity/         → JPA entities (AlbumEntity, UserEntity, RoleEntity, PermissionEntity, etc.)
dto/request/    → Request DTOs
dto/response/   → Response DTOs
config/         → Spring configuration (SecurityConfig, WebConfig, JwtConfig, OpenAPIConfig, DataInitializer)
filter/         → JwtAuthenticationFilter (OncePerRequestFilter)
annotation/     → @RequirePermission, @RequireRole, @IgnoreResponseWrap
aspect/         → PermissionAspect (AOP for @RequirePermission / @RequireRole)
exception/      → BusinessException, GlobalExceptionHandler
common/         → ServiceResult<T> (unified API response wrapper)
enums/          → Enums (ResultCode, PermissionType)
util/           → JwtUtil, UserUtils, ImageUtils
factory/        → PermissionFactory
```

**Key architectural patterns:**

- **Authentication**: JWT-based (Spring Security + jjwt library). Login at `/api/auth/login` returns `accessToken` + `refreshToken`. The `JwtAuthenticationFilter` checks `Authorization: Bearer <token>` on every request (except whitelisted paths).
- **API Response Wrapper**: All controllers return `ServiceResult<T>` — a uniform envelope with `{success, message, code, data, timestamp}`.
- **Authorization**: Two-layer — (1) Spring Security role-based via `hasRole()`, (2) custom annotation-based via `@RequirePermission("user:read")` or `@RequireRole("ROLE_ADMIN")` enforced by `PermissionAspect`.
- **RBAC**: Users → Roles (many-to-many) → Permissions (many-to-many). Seeded by `DataInitializer` on first startup.
- **Global Exception Handling**: `GlobalExceptionHandler` catches all exceptions and returns `ServiceResult` with appropriate HTTP status codes.
- **API Path Convention**: All controller endpoints are under `/api/<module>` (e.g., `/api/album/insert`, `/api/patient/list`).

### Domain Modules (Backend)

Each domain follows the same pattern:

| Domain | Controller | Service | Repository | Entity |
|--------|-----------|---------|------------|--------|
| User/Auth | UserController | UserService | UserRepository | UserEntity |
| Role | RoleController | RoleService | RoleRepository | RoleEntity |
| Permission | PermissionController | PermissionService | PermissionRepository | PermissionEntity |
| Patient | PatientController | PatientService | PatientRepository | PatientEntity |
| Doctor | DoctorController | DoctorService | DoctorRepository | DoctorEntity |
| Appointment | AppointmentController | AppointmentService | AppointmentRepository | AppointmentEntity |
| Medical Record | MedicalRecordController | MedicalRecordService | MedicalRecordRepository | MedicalRecordEntity |
| Medicine | MedicineController | MedicineService | MedicineRepository | MedicineEntity |
| Album (Photo Album) | AlbumController | AlbumService | AlbumRepository | AlbumEntity / PhotoEntity |
| Phone | PhoneController | PhoneService | PhoneRepository | — |
| Menu | MenuController | MenuService | MenuRepository | MenuEntity |

### Frontend (JavaFX) — Package layout

```
App.java                 → Main Application class, navigation hub (FXML-based navigation)
Launcher.java            → Launcher (main entry point without JavaFX module constraints)
controller/              → FXML controllers (LoginController, HomeController, PatientManagementController, etc.)
service/                 → ApiService (singleton, async API caller), MedicalApiService, SystemApiService
model/                   → Data model classes (Patient, Doctor, Medicine, Appointment, MedicalRecord, Photo, PhotoAlbum)
config/                  → ApiClientConfig (reads db.properties for API base URL)
cache/                   → MenuCache, PageCache
factory/                 → ContentLoaderFactory, MenuItemFactory
menu/                    → AppPage enum (maps pages to FXML paths / window sizes), ResultCode
util/                    → UserSession (stored username/token), DialogManager, IconUtil
ai/                      → AIService
exception/               → ApiServiceException

resources/com/example/view/ → FXML layout files (login.fxml, home.fxml, patient-management.fxml, etc.)
resources/com/example/css/  → CSS stylesheets (photo.css, mindmap-style.css)
resources/db.properties      → Configuration (API base URL, DB connection, timeouts)
```

**Key architectural patterns:**

- **Navigation**: `App.navigateTo(AppPage.XXX)` switches scenes. `AppPage` enum maps pages to FXML paths, window dimensions, stage style, and draggable behavior.
- **API Layer**: `ApiService` is a thread-safe singleton. Uses Swagger-generated `DefaultApi` client for backend communication. Async calls via `callAsync()` with callbacks (success/error/loading). `ApiClientConfig` reads base URL from `db.properties` (supports dev/test/prod profiles via `api.profile` system property).
- **User Session**: `UserSession` stores login state (username, token) — checked across controllers.
- **FXML**: Each view is an `.fxml` file with a corresponding `*Controller.java` class. Controllers use `@FXML` annotations for UI element binding.
- **Dynamic Menus**: Sidebar menus fetched from `/api/menu/findByMenu` on login (RBAC-filtered). `HomeController` renders `MenuItemFactory`-created buttons, and `ContentLoaderFactory` loads FXML by route on click.

## JavaFX Runtime Configuration

The frontend uses Java 21 modules (`module-info.java`). Run with:

```bash
cd frontend && mvn clean compile javafx:run
```

The `javafx-maven-plugin` and `maven-compiler-plugin` are configured with:
- `--add-reads com.example.javafxdemo=ALL-UNNAMED` — allow access to classpath (unnamed) libraries
- `--add-opens` for all controller/model/service packages to `javafx.fxml` and `com.fasterxml.jackson.databind`
- `--add-opens java.base/java.lang=ALL-UNNAMED` etc. for library compatibility

**Key `module-info.java` exports** for FXML + Jackson/Gson:
- `opens com.example.controller to javafx.fxml` — FXML controller reflection
- `opens io.swagger.client.model to com.fasterxml.jackson.databind, com.google.gson` — Swagger model serialization
- `requires` entries for all named modules (Jackson, SLF4J, Logback, Commons Lang3, Jakarta, OkHttp, Gson, Okio)

When adding new packages or FXML views, ensure they're either `exports`'d or `opens`'d in `module-info.java`.

## Security Whitelist (Backend)

The following paths are public (no JWT required):
- `/api/auth/**` — login, register
- `/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-resources/**`, `/webjars/**`
- `/uploads/**` — static file access
- `/error/**`

Everything else requires `Authorization: Bearer <token>`.

## File Upload

- Configured via `file.upload.path` in `application.yaml`
- Uploaded files accessible via `http://localhost:8080/uploads/<filename>`
- Static resource serving configured in `StaticResourceConfig`

## Database Tables

Auto-generated by Hibernate (`ddl-auto: update`). Key tables:
- `users` — User accounts
- `roles` — RBAC roles
- `permissions` — Fine-grained permissions
- `user_roles` — User-role join table
- `role_permissions` — Role-permission join table
- `patients`, `doctors`, `appointments`, `medical_records`, `medicines`
- `albums`, `photos` — Photo albums
- `menus` — Menu tree structure
