# QuanCaPhe — Quản lý quán cà phê (dành cho junior)

README đầy đủ cho developer, reviewer và các bạn junior mới tham gia dự án.

Tài liệu này gồm:
- Mô tả tổng quan
- Công nghệ sử dụng và phiên bản khuyến nghị
- Kiến trúc và cấu trúc package
- Danh sách tính năng và luồng nghiệp vụ
- Cách chạy local (quickstart + cấu hình)
- Quy ước dev, lộ trình refactor và gợi ý testing

Hãy giữ file này luôn cập nhật — đây là nguồn thông tin duy nhất cho việc onboarding.

---

## 1. Tóm tắt dự án

**Tên dự án:** QuanCaPhe (Coffee Shop Management)  
**Mục tiêu:** ứng dụng web render server (Spring Boot + Thymeleaf) để quản lý vận hành quán cà phê nhỏ: bán hàng theo bàn, quản lý nhân viên, thực đơn & thiết bị, và báo cáo (thu/chi, bán hàng theo ngày, nhân sự).

Đối tượng: junior học full-stack Spring Boot, tập trung vào backend (service/repository) và render server-side.

---

## 2. Tech stack & phiên bản khuyến nghị

- **Java:** 17 (LTS) — compile và chạy bằng JVM Java 17  
- **Spring Boot:** 3.x (xem `pom.xml` để biết phiên bản chính xác)  
- **Spring MVC** + **Thymeleaf** (UI server-side)  
- **Spring Data JPA** (Hibernate) cho ORM  
- **Spring Security** cho xác thực & phân quyền  
- **Database:** MySQL (dev); khuyến nghị H2 cho test nhẹ  
- **Build:** Maven (dùng wrapper `./mvnw`)  
- **Dev tools:** tùy chọn `spring-boot-devtools` để hot reload

Lưu ý:
- Dùng đúng phiên bản dependency trong `pom.xml`. Dự án được build với Spring Boot 3.x và Java 17.

---

## 3. Kiến trúc & nguyên tắc thiết kế

Dự án theo kiến trúc nhiều lớp:

- **Controller (Web layer)** — nhận HTTP request, validate cơ bản, gọi Service và trả về Thymeleaf view + model. Controller nên mỏng.
- **Service (Business layer)** — xử lý logic nghiệp vụ: tính toán, chuyển đổi thời gian, tổng hợp, mapping dữ liệu.
- **Repository (Data access layer)** — JPA repository & native SQL cho báo cáo. Repository trả về entity hoặc raw objects để service mapping.
- **Entity (Domain model)** — JPA entity đại diện bảng DB.
- **DTO (Data Transfer Objects)** — object đơn giản/immutable để truyền dữ liệu giữa các lớp.

Nguyên tắc để dễ bảo trì:
- Controller mỏng; logic & mapping nằm ở Service.  
- Báo cáo theo ngày: ưu tiên native SQL `DATE(column)` để nhóm dữ liệu; service map `Object[]` → DTO. Tránh JPQL dùng `function('date', ...)` trong constructor expression.  
- Template Thymeleaf tránh SpEL phức tạp; dùng `#aggregates.sum(list.![field])` để tính tổng.  
- Query & mapping nên có chú thích ở service để dễ review.

---

## 4. Cấu trúc dự án (khuyến nghị)

Các package chính (đã có trong code):

```
src/main/java/com/example/demo
├─ controller/              # Controller cho admin/sales/... (không gồm report)
├─ report/                  # Module báo cáo (tách riêng)
│  ├─ controller/
│  ├─ dto/
│  ├─ repository/
│  └─ service/
├─ service/                 # Service nghiệp vụ (non-report)
├─ repository/              # JPA repository cho domain (Ban, HoaDon, NhanVien,...)
├─ entity/                  # JPA entities
├─ dto/                     # DTO dùng chung / form
└─ security/                # Security config / user details
```

Templates:

```
src/main/resources/templates/
├─ fragments/   # head, header, footer, sidebar
├─ layout/      # base layout dùng fragments
├─ admin/       # trang admin (bao gồm report UI)
├─ sales/       # UI bán hàng
└─ ...
```

Static assets: `src/main/resources/static/` (css/js/images).

---

## 5. Entity & repository quan trọng (tóm tắt)

- `HoaDon` — hóa đơn (maHoaDon, ngayGioTao, ngayThanhToan, tongTien, trangThai)  
- `ChiTietHoaDon` — chi tiết hóa đơn (món, số lượng, giá)  
- `Ban` — bàn  
- `NhanVien` — nhân viên (`enabled` để phân biệt đang làm / nghỉ)  
- Repository: `HoaDonRepository`, `NhanVienRepository`, `ChiTieuRepository` (chi tiêu)

Ghi chú báo cáo:
- Group theo ngày + tổng tiền nên làm bằng native SQL `DATE(column)` và map tại service.

---

## 6. Tính năng & luồng chính

**Bán hàng theo bàn:**
1. Mở bàn → tạo `HoaDon`.  
2. Thêm `ChiTietHoaDon` (món).  
3. Thanh toán → set `trangThai = 'DA_THANH_TOAN'` và `ngayThanhToan = now()`.

**Nhân viên:**
- Admin tạo/cập nhật `NhanVien`.  
- `NhanVien` có thể gắn `TaiKhoan`.  
- `enabled` dùng cho báo cáo nhân sự.

**Báo cáo (UC11):**
- UI: chọn khoảng ngày + loại báo cáo `{FINANCE, SALES, STAFF}` + “Xem” + “In”.  
- Thu/Chi: tổng doanh thu và chi tiêu theo ngày.  
- Bán hàng theo ngày: số hóa đơn + doanh thu.  
- Nhân viên: số lượng active/inactive.

Ghi chú triển khai:
- Controller cung cấp `filter` + dữ liệu báo cáo cho view.  
- Service map dữ liệu và trả về list DTO.  
- Template dùng `#aggregates.sum` để tính tổng.

---

## 7. Chạy local — Quickstart

**Yêu cầu:**
- Java 17+  
- MySQL chạy local  
- Tạo database `quancaphe` và user (hoặc chỉnh `application.properties`)

**Bước 1:** cấu hình DB trong `src/main/resources/application.properties`:

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

**Bước 2:** build & chạy:

```bash
./mvnw -DskipTests package
./mvnw spring-boot:run
```

**Bước 3:** mở trình duyệt:
- `http://localhost:8080/login` — trang login  
- `http://localhost:8080/admin/report` — trang báo cáo (admin)

Lưu ý:
- `ddl-auto=update` chỉ dùng cho dev. Production nên dùng Flyway/Liquibase.

---

## 8. Quy ước dev & best practices

**Branch:**
- `main`: stable release  
- `feature/<name>`: phát triển tính năng  
- `refactor/<name>`: refactor

**Commit message:**
- Dùng prefix ngắn: `feat(...)`, `fix(...)`, `refactor(...)`

**Coding rules:**
- Controller mỏng; logic đặt ở Service.  
- Repository chỉ trả dữ liệu; mapping nằm ở Service.  
- Tránh JPQL dùng function phụ thuộc dialect trong constructor expression; ưu tiên native SQL.

**Thymeleaf:**
- Tránh SpEL phức tạp; dùng `#aggregates.sum(list.![field])`.  
- Dùng fragments cho header/footer/sidebar với `th:replace="~{fragments/head :: head}"`.

**Testing:**
- Ưu tiên unit test cho mapping & logic tổng hợp.  
- Dùng H2 cho integration test nhẹ (lưu ý behavior của DATE).

---

## 9. Lộ trình refactor (khuyến nghị)

**Đợt 1 — Audit & Clean:** xoá code dư, fix Thymeleaf parsing.  
**Đợt 2 — Package Reorg:** tách report module (done).  
**Đợt 3 — Service Split:** tách finance/sales/staff service (done).  
**Đợt 4 — Repository Consolidation:** gom native report SQL vào `report.repository` (next).  
**Đợt 5 — Template Fragments:** tách `admin/report` thành partials cho từng report.  
**Đợt 6 — Tests:** thêm unit test cho report mapping + smoke test.

---

## 10. Troubleshooting & lỗi thường gặp

- App fail startup với `Query validation failed` ở repository:
  - Kiểm tra JPQL có `function('date', ...)` hoặc constructor expression dùng function DB-specific. Nên chuyển sang native SQL.

- Tổng bị `null` hoặc lỗi parse template:
  - Đảm bảo model luôn trả list (không null) và dùng `#aggregates.sum(list.![field])`.

---

## 11. Checklist khi PR

- Chạy `./mvnw -DskipTests package` và test UI cơ bản.  
- Change scope gọn (1 feature/PR).  
- Thêm unit test khi sửa logic nghiệp vụ.  
- Nếu đổi schema, thêm migration ở `src/main/resources/db/migration`.

---

Nếu bạn muốn, mình có thể:
- Thêm `scripts/` với SQL seed nhanh.  
- Viết 2 unit test mẫu cho báo cáo bán hàng.  
- Tách template report thành các fragment nhỏ.

Hãy nói phần bạn muốn làm tiếp và mình sẽ triển khai.
