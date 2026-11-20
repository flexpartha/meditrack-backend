# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**MediTrack** — Clinic appointment & billing API (Spring Boot 4.1.0, Java 17, Maven). This starter is intentionally rough and violates most conventions; the task is to refactor and extend it.

## Build & Run Commands

| Task | Command |
|------|---------|
| Build | `mvn clean compile` |
| Run tests | `mvn test` |
| Run single test | `mvn test -Dtest=ClassName#methodName` |
| Run application | `mvn spring-boot:run` |
| Package JAR | `mvn package` |
| Add Maven wrapper (first refactor task) | `mvn -N wrapper:wrapper` |

## Database Setup

1. Run `db/meditrack_schema.sql` in MySQL Workbench (creates `meditrack_db` + seed data).
2. Update `src/main/resources/application.properties` if DB credentials differ.

## Project Structure

```
src/main/java/com/bharath/meditrack/
├── MediTrackApplication.java          # Spring Boot entry point
├── controller/
│   ├── AppointmentController.java     # Booking/cancel endpoints (ALL business logic here)
│   ├── CatalogController.java         # Doctors & specialties endpoints
│   └── PatientController.java         # Patient endpoints
├── model/
│   ├── Appointment.java               # JPA entity (uses @Data, double for money, free-text status)
│   ├── AppointmentService.java        # JPA entity (double for money)
│   ├── Doctor.java                    # JPA entity (double for money, EAGER fetch)
│   ├── Patient.java                   # JPA entity (uses @Data)
│   ├── Payment.java                   # JPA entity (double for money)
│   └── Specialty.java                 # JPA entity (uses @Data)
└── repo/
    ├── AppointmentRepository.java     # Custom countByDoctorAndScheduledDate
    ├── DoctorRepository.java
    ├── PatientRepository.java
    └── SpecialtyRepository.java
```

## Key Architectural Issues (Intentional)

Per the README, these are the deliberate anti-patterns to fix:

1. **Money as `double`** — All monetary fields (`consultation_fee`, `total_amount`, `unit_price`, `subtotal`, `amount`) use `double` instead of `BigDecimal`
2. **`@Data` on JPA entities** — Entities expose all fields via getters/setters; returned directly from controllers (no DTOs)
3. **No service layer** — All business logic lives in controllers
4. **No exception handling** — Missing rows return `null`; errors throw generic `RuntimeException` (HTTP 500)
5. **Field injection** — Uses `@Autowired` on fields instead of constructor injection
6. **Insecure config** — `ddl-auto=update` and plain-text DB password in `application.properties`
7. **Free-text status** — `Appointment.status` is a `String` with no enum or transition rules
8. **Broken cancel logic** — Cancel doesn't restore doctor's slot or refund payment
9. **EAGER fetching everywhere** — All `@ManyToOne` and `@OneToMany` use `FetchType.EAGER`
10. **No validation, tests, or API docs**

## Conventions to Follow (from User Instructions)

- **DI**: Always use `@RequiredArgsConstructor` with `final` fields; never use `@Autowired`
- **Entities**: Annotate with `@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor`; never `@Data`
- **DTOs**: Annotate with `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
- **Money**: Always use `BigDecimal` for money fields; never `double` or `float`
- **Tests**: JUnit 5, Mockito, AssertJ, MockMvc; always use AssertJ `assertThat`; never `assertEquals`
- **Communication**: Show diffs, not full rewrites; ask before deleting any file; be direct and technical

## Project-Specific Requirements (Enforced)

- **Constructor injection only** — Use `@RequiredArgsConstructor` with `final` fields; never `@Autowired` on fields
- **BigDecimal for all money** — Every monetary field (entities, DTOs, calculations) uses `BigDecimal`; never `double` or `float`
- **DTOs in/out of controllers** — Controllers accept and return DTOs only; never expose JPA entities directly
- **Thin controllers** — All business logic lives in a service layer; controllers only handle HTTP mapping
- **API versioning** — All endpoints under `/api/v1/` prefix
- **Centralised exception handling** — Use `@RestControllerAdvice` with a consistent `ErrorResponse` JSON format for all errors
- **Schema management** — `spring.jpa.hibernate.ddl-auto=validate` (tables created/managed only via `db/meditrack_schema.sql`)

## API Endpoints (Current)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/patients` | List all patients |
| POST | `/patients` | Register patient (no validation) |
| GET | `/specialties` | List all specialties |
| POST | `/specialties` | Create specialty |
| GET | `/doctors` | List all doctors |
| GET | `/doctors/{id}` | Get doctor by ID (returns null on miss) |
| POST | `/doctors` | Create doctor |
| GET | `/appointments` | List all appointments |
| GET | `/appointments/{id}` | Get appointment by ID |
| POST | `/appointments/book` | Book appointment (`patientId`, `doctorId`, `date` params) |
| POST | `/appointments/{id}/cancel` | Cancel appointment (no slot restore/refund) |

## Database Schema (from `db/meditrack_schema.sql`)

Tables: `specialties`, `doctors`, `patients`, `appointments`, `appointment_services`, `payments`

All monetary columns use `DECIMAL` in the database — the Java entities incorrectly map these to `double`.