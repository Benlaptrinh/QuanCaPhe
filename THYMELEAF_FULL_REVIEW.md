# Đánh giá toàn bộ Thymeleaf templates (Review)

Ngắn gọn: đây là báo cáo review hiện trạng thư mục `templates/` của project, những gì đã làm, điểm mạnh, lỗi/nhược điểm đang có và khuyến nghị hành động tiếp theo. Viết bằng tiếng Việt, dành cho reviewer/junior dev.

1) Bản đồ templates (hiện tại)
--------------------------------
- `src/main/resources/templates/`
  - `login.html` — form login (độc lập, dùng `fragments/head`)
  - `layout/base.html` — layout tổng, quản lý sidebar + content bằng model fragment (`sidebarFragment` và `contentFragment`)
  - `layout/fragments/`
    - `head.html` — fragment head
    - `header.html` — header (tối giản)
    - `footer.html` — footer
    - `sidebar-admin.html` — sidebar cho Admin (link tới nhiều chức năng)
    - `sidebar-staff.html` — sidebar cho Staff
  - `admin/` (Admin views)
    - `dashboard.html` — fragment `content` hiển thị chào mừng
    - `employees.html` — (placeholder, ban đầu)
    - `employees_list.html` — danh sách nhân viên (nội dung mới)
    - `employees_create.html` — form thêm nhân viên
    - `employees_create.html` — form thêm (nội dung)
    - `employees_edit.html` — (đã xóa, không dùng)
    - `sales.html`, `equipment.html`, `warehouse.html`, `menu.html`, `marketing.html`, `budget.html`, `reports.html` — placeholder pages
    - `users/*` — admin user CRUD views (list/create/edit) — dùng ở `AdminUserController`
  - `staff/`
    - `home.html` — staff home (fragment `content`)
    - `sales.html` — placeholder staff sales
  - `profile/`
    - `view.html` — profile view (fragment `content`)
    - `edit.html` — profile edit form (fragment `content`)

2) Mapping controller ↔ view (tóm tắt)
--------------------------------------
- `AuthController` → `login.html`
- `AdminController` → `/admin/dashboard` → returns `layout/base` with `contentFragment=admin/dashboard`
- `StaffController` → `/staff/home` → returns `layout/base` with `contentFragment=staff/home`
- `ProfileController` → `/profile`, `/profile/edit` → returns `layout/base` with `contentFragment=profile/view|edit` và `sidebarFragment` chọn theo role
- `AdminUserController` → `/admin/users/*` (user auth CRUD)
- `AdminEmployeesController` → `/admin/employees/*` (employee CRUD: list/create/edit/delete)

3) Những điểm tốt hiện tại
--------------------------
- Thiết kế layout + fragments chuẩn: chỉ cần sửa header/sidebar ở 1 chỗ.  
- Quy ước dùng `layout/base` + truyền fragment tên qua model (`contentFragment`, `sidebarFragment`) rõ ràng, linh hoạt.  
- Phần security + redirect theo role đã setup đúng (session-based).  
- Backend có xử lý: hash password, validate username unique, auto-set salary từ `ChucVu` khi lưu `NhanVien`.  
- Đã implement CRUD cơ bản cho nhân viên (controller + templates cơ bản), delete transactional xóa cả TaiKhoan nếu có.

4) Vấn đề / bug còn tồn tại
---------------------------
- Fragment resolution: đã sửa nhiều lần (dùng `~{${contentFragment} :: content}` pattern). Hiện ổn nhưng nhạy cảm với cú pháp, cần giữ chuẩn.  
- Template consistency:
  - Một số file dùng `admin/users/*` còn một số mới dùng `admin/employees_*`. Nên thống nhất tên/đường dẫn (users vs employees) để tránh nhầm controller/template.  
  - `admin/employees.html` (placeholder) và `admin/employees_list.html` tồn tại cạnh nhau — cần loại bỏ placeholder hoặc hợp nhất.  
- Form validation & feedback:
  - Hiện chỉ có flash `error`/`message` simple. Chưa có binding error per-field, chưa có server-side validation messages chi tiết.  
- UX:
  - Forms hiện dùng POST về controller, nhưng chưa thêm CSRF token (nếu bật CSRF phải thêm token vào tất cả form). Hiện CSRF bị disable trong `SecurityConfig` — **phải bật lại trước khi deploy**.  
  - No image upload implementation (avatar uses URL input). Nếu cần upload file, phải handle multipart.  
- Data seeding:
  - Admin may not have `NhanVien` row; profile shows empty fields. Cần seed demo NhanVien for admin or allow Admin to create employee mapped to admin.  
- Duplicate code:
  - Sidebar markup duplicated for admin/staff fragments; acceptable but could be refactored to smaller fragments.

5) Security considerations
-------------------------
- Passwords hashed with BCrypt on save — OK.  
- Username uniqueness checked in service (throws IllegalArgumentException) — OK but should map to user-friendly error handling.  
- Delete flow currently deletes TaiKhoan and NhanVien together — ok for SRS, but production often prefers soft-delete. Document decision.

6) Recommended immediate tasks (priority)
-----------------------------------------
1. Thống nhất tên templates: dùng `admin/employees_*` cho employee management (xóa `admin/employees.html` placeholder).  
2. Thêm view cho `admin/employees_list.html` là trang chính cho quản lý nhân viên; link sidebar `/admin/employees` → controller `AdminEmployeesController.list` (đã tạo).  
3. Bật CSRF (production) và thêm CSRF token vào tất cả forms:
   - `<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />`
4. Hoàn thiện validation:
   - Dùng `@Valid` + `BindingResult` trong controller, hiển thị errors trên form.  
5. Add server-side and UI flash message display in `layout/base` so messages persist across pages (một chỗ show `message` / `error`).
6. Add search + pagination for employee list (repo methods + `Pageable`).

7) Files quan trọng để review (mở nhanh)
---------------------------------------
- `src/main/resources/templates/layout/base.html` — layout tổng, fragment injection.  
- `src/main/resources/templates/fragments/sidebar-admin.html` — menu admin.  
- `src/main/resources/templates/profile/view.html` — profile view, xem logic hiển thị lương.  
- `src/main/java/com/example/demo/controller/AdminEmployeesController.java` — employee CRUD controller (logic tạo/tự gắn TaiKhoan tùy chọn).  
- `src/main/java/com/example/demo/service/impl/TaiKhoanServiceImpl.java` — validate username + hash.  
- `src/main/java/com/example/demo/service/impl/NhanVienServiceImpl.java` — auto-set salary, delete transactional.

8) Next steps tôi sẽ làm nếu bạn đồng ý
--------------------------------------
- Thực hiện search + pagination cho `admin/employees` (repo, controller, template).  
- Thêm validation + hiển thị lỗi per-field cho create/edit employee.  
- Thêm CSRF tokens vào forms và bật CSRF (mình sẽ đảm bảo forms include token).  
- Tinh chỉnh UI list/create/edit để giống SRS (table columns: Họ tên, Chức vụ, Lương, Actions).  

Kết luận
--------
Hiện tại Thymeleaf layout & flow đã hoàn thiện cơ bản, phù hợp SRS. Cần thêm validation, CSRF, và thống nhất templates (employees vs users) để hoàn thiện. Nếu OK mình sẽ tiếp tục với nhiệm vụ ưu tiên: "search + pagination" hoặc "validation + flash + CSRF" — bạn chọn.  


