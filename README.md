# QuanCaPhe — Coffee Shop Management (Junior-friendly)

Comprehensive project README for developers, reviewers and junior contributors.

This document contains:
- A high-level description
- Exact technology choices and recommended versions
- Architecture and package layout
- Feature list and business flows
- How to run locally (quickstart + configuration)
- Developer conventions, refactor roadmap and testing suggestions

Keep this file up to date — it's the single source of truth for onboarding.

---

## Project summary

**Project name:** QuanCaPhe (Coffee Shop Management)  
**Purpose:** a server-rendered web application (Spring Boot + Thymeleaf) to manage operations of a small coffee shop: table-based sales, employee management, menu & equipment management, and reporting (finance, sales-by-day, staff).

Target audience: junior engineers learning a full-stack Spring Boot application with a focus on backend responsibilities (service and repository layers) and server-side rendering.

---

## 1. Tech stack & exact recommendations

- **Java:** 17 (LTS) — compile and run with Java 17 JVM  
- **Spring Boot:** 3.x (check `pom.xml` for exact version)  
- **Spring MVC** + **Thymeleaf** (server-side UI)  
- **Spring Data JPA** (Hibernate) for ORM  
- **Spring Security** for authentication & role-based access  
- **Database:** MySQL (development); H2 for lightweight tests is recommended  
- **Build:** Maven (use wrapper `./mvnw`)  
- **Dev tools:** optional `spring-boot-devtools` for hot reload during dev

Notes:
- Use exact dependency versions in `pom.xml` when reproducing environment. The project was built against Spring Boot 3.x and Java 17.

---

## 2. Architecture & design principles

This project follows a layered architecture:

- **Controller (Web layer)** — Accepts HTTP requests, validates basic input, calls the Service layer and returns Thymeleaf views with model attributes. Controllers should be thin.
- **Service (Business layer)** — Contains the domain/business logic: calculations, date-time conversions, aggregation, transaction boundaries, mapping raw query results to DTOs.
- **Repository (Data access layer)** — JPA repositories and native SQL queries for DB-specific aggregations (reports). Repository methods return domain entities or raw objects for service mapping.
- **Entity (Domain model)** — JPA-mapped entities representing DB tables.
- **DTO (Data Transfer Objects)** — Immutable or simple objects used to transfer data between service and presentation layers (forms, report rows).

Design rules for maintainability:
- Keep Controllers thin — move business logic & mapping to Services.  
- When reporting by date, prefer native SQL grouping on the DB side (stable) and map `Object[]` → DTOs in service. Avoid fragile JPQL using `function('date', ...)` in constructor expression.  
- Templates (Thymeleaf) should avoid complex SpEL; use `#aggregates.sum(list.![field])` for totals.  
- Keep queries and mapping documented in service code to ease review.

---

## 3. Project structure (recommended)

Key packages (present in codebase):

```
src/main/java/com/example/demo
├─ controller/              # Non-report controllers (admin pages, sales UI, etc.)
├─ report/                  # Report module (self-contained)
│  ├─ controller/
│  ├─ dto/
│  ├─ repository/
│  └─ service/
├─ service/                 # Application services (non-report)
├─ repository/              # Domain JPA repositories (Ban, HoaDon, NhanVien, ...)
├─ entity/                  # JPA entities
├─ dto/                     # Shared DTOs / forms
└─ security/                # Security config / user details
```

Templates:

```
src/main/resources/templates/
├─ fragments/   # head, header, footer, sidebar fragments
├─ layout/      # base layout using fragments
├─ admin/       # admin pages including report UI
├─ sales/       # sales UI
└─ ... 
```

Static assets: `src/main/resources/static/` (css/js/images).

---

## 4. Important entities & repositories (quick)

- `HoaDon` — invoice (maHoaDon, ngayGioTao, ngayThanhToan, tongTien, trangThai)  
- `ChiTietHoaDon` — invoice lines (menu item, quantity, price)  
- `Ban` — table  
- `NhanVien` — employee (enabled flag denotes active / inactive)  
- Repositories: `HoaDonRepository`, `NhanVienRepository`, `ChiTieuRepository` (for expenses)

Reporting notes:
- Grouping per day and sums are best done by native SQL with `DATE(column)` for MySQL and mapped in service.

---

## 5. Features & key flows

Sales flow (table-based):
1. Create `HoaDon` when a table opens.  
2. Add `ChiTietHoaDon` (menu items) to the invoice.  
3. On checkout, set `trangThai = 'DA_THANH_TOAN'` and `ngayThanhToan = now()`.  

Employee flow:
- Admin can create/update `NhanVien`.  
- Each `NhanVien` may be linked to a `TaiKhoan` (account).  
- `enabled` boolean indicates active/inactive (used by staff reports).

Reports (UC11):
- UI: Date range + radio type `{FINANCE, SALES, STAFF}` + “Xem” + “In”.  
- Finance (Thu/Chi): combine invoice totals and expense records per day.  
- Sales by day: date, invoice count, daily revenue.  
- Staff: active vs inactive counts.

Implementation notes:
- Controller supplies `filter` and selected report data to the model.  
- Service performs mapping and returns DTO lists for templates.  
- Template uses `#aggregates.sum` for totals and `#temporals.format` to render dates.

---

## 6. How to run locally — Quickstart

Prerequisites:
- Java 17+ installed  
- MySQL server running locally  
- Create database `quancaphe` and user credentials (or change `application.properties`)

1) Configure database in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/quancaphe?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.thymeleaf.cache=false
server.port=8080
```

2) Build and run:

```bash
./mvnw -DskipTests package
./mvnw spring-boot:run
```

3) Open browser:
- `http://localhost:8080/login` — login page  
- `http://localhost:8080/admin/report` — report page (admin)

Notes:
- Use `ddl-auto=update` only for local dev. Use Flyway/Liquibase for production.

---

## 7. Developer conventions & best practices

Branching:
- `main`: stable release  
- `feature/<name>`: feature development  
- `refactor/<name>`: refactor-only changes

Commit messages:
- Use conventional, short prefixes: `feat(...)`, `fix(...)`, `refactor(...)`

Coding rules:
- Keep controllers thin; services handle logic and mapping.  
- Repository returns minimal data; avoid putting mapping logic in repositories.  
- Avoid JPQL constructs that depend on dialect-specific functions in constructor expressions. Use native SQL for stable aggregation queries.

Thymeleaf rules:
- Avoid SpEL expressions that Thymeleaf cannot parse; use `#aggregates.sum(list.![field])` for totals.  
- Use fragments for header/footer/sidebar and include them with `th:replace="~{fragments/head :: head}"`.

Testing:
- Focus unit tests on Service mapping & aggregation logic.  
- Use H2 for lightweight integration tests when testing repository queries (with caution for DATE behavior).

---

## 8. Refactor roadmap (recommended)

Short plan (non-breaking):
- **Đợt 1 — Audit & Clean:** remove unused DTOs/services, fix Thymeleaf parsing.  
- **Đợt 2 — Package Reorg:** move report code to `report` module (done).  
- **Đợt 3 — Service Split:** separate finance/sales/staff services (done).  
- **Đợt 4 — Repository Consolidation:** put native report SQL into `report.repository` (next).  
- **Đợt 5 — Template Fragments:** split `admin/report` into small partials per report.  
- **Đợt 6 — Tests:** add unit tests for report mapping and a small integration smoke test.

---

## 9. Troubleshooting & common issues

- If app fails at startup with `Query validation failed` referencing a repository:
  - Inspect JPQL for `function('date', ...)` or constructor expressions that use DB-specific functions. Move those queries to native SQL.

- If totals show `null` or template parsing errors:
  - Ensure model supplies `List.of()` (not `null`) and use `#aggregates.sum(list.![field])`.

---

## 10. Contributing / PR checklist

- Run `./mvnw -DskipTests package` and manual UI smoke test.  
- Keep changes scoped (one feature per PR).  
- Add unit tests for service logic when adding/changing business logic.  
- Document DB changes and add migrations under `src/main/resources/db/migration` if schema changes are required.

---

If you want, I can also:
- Add a `scripts/` folder with a quick SQL seed script.  
- Add two sample unit tests for the sales report mapping.  
- Split the report templates into fragments as planned.

Tell me which follow-up you'd like and I'll implement it.


