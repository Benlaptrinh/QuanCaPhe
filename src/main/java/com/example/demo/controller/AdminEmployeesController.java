package com.example.demo.controller;



import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.repository.ChucVuRepository;
import com.example.demo.service.NhanVienService;
import com.example.demo.service.TaiKhoanService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import com.example.demo.enums.Role;

@Controller
@RequestMapping("/admin/employees")
public class AdminEmployeesController {

    private final NhanVienService nhanVienService;
    private final TaiKhoanService taiKhoanService;
    private final ChucVuRepository chucVuRepository;

    public AdminEmployeesController(NhanVienService nhanVienService, TaiKhoanService taiKhoanService, ChucVuRepository chucVuRepository) {
        this.nhanVienService = nhanVienService;
        this.taiKhoanService = taiKhoanService;
        this.chucVuRepository = chucVuRepository;
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model, Authentication auth) {
        List<NhanVien> list;
        if (q != null && !q.isBlank()) {
            list = nhanVienService.findByHoTenContaining(q);
        } else {
            list = nhanVienService.findAll();
        }
        model.addAttribute("nhanViens", list);
        model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
        model.addAttribute("contentFragment", "admin/employees_list");
        model.addAttribute("username", auth == null ? "anonymous" : auth.getName());
        return "layout/base";
    }

    @GetMapping("/create")
    public String createForm(Model model, Authentication auth) {
        model.addAttribute("nhanVien", new NhanVien());
        model.addAttribute("chucVus", chucVuRepository.findAll());
        model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
        model.addAttribute("contentFragment", "admin/employees_create");
        model.addAttribute("username", auth == null ? "anonymous" : auth.getName());
        return "layout/base";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute NhanVien nhanVien,
                         @RequestParam(value = "chucVuId", required = false) Long chucVuId,
                         @RequestParam(value = "tenDangNhap", required = false) String tenDangNhap,
                         @RequestParam(value = "matKhau", required = false) String matKhau,
                         @RequestParam(value = "role", required = false) String role,
                         RedirectAttributes redirectAttributes) {
        if (chucVuId != null) {
            chucVuRepository.findById(chucVuId).ifPresent(nhanVien::setChucVu);
        }
        try {
            if (tenDangNhap != null && !tenDangNhap.isBlank()) {
                if (matKhau == null || matKhau.isBlank()) {
                    throw new IllegalArgumentException("Password required when creating account");
                }
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(tenDangNhap);
                tk.setMatKhau(matKhau);
                try {
                    Role r = (role == null || role.isBlank()) ? Role.NHANVIEN : Role.valueOf(role);
                    tk.setQuyenHan(r);
                } catch (IllegalArgumentException ex) {
                    tk.setQuyenHan(Role.NHANVIEN);
                }
                tk.setEnabled(true);
                TaiKhoan savedTk = taiKhoanService.save(tk);
                nhanVien.setTaiKhoan(savedTk);
            }
            nhanVienService.save(nhanVien);
            redirectAttributes.addFlashAttribute("message", "Thêm nhân viên thành công");
            return "redirect:/admin/employees";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/employees/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication auth) {
        NhanVien nv = nhanVienService.findById(id).orElse(new NhanVien());
        model.addAttribute("nhanVien", nv);
        model.addAttribute("chucVus", chucVuRepository.findAll());
        model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
        model.addAttribute("contentFragment", "admin/employees_edit");
        model.addAttribute("username", auth == null ? "anonymous" : auth.getName());
        return "layout/base";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute NhanVien nhanVien,
                       @RequestParam(value = "chucVuId", required = false) Long chucVuId,
                       @RequestParam(value = "tenDangNhap", required = false) String tenDangNhap,
                       @RequestParam(value = "matKhau", required = false) String matKhau,
                       @RequestParam(value = "role", required = false) String role,
                       RedirectAttributes redirectAttributes) {
        NhanVien existing = nhanVienService.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("error", "Nhân viên không tồn tại");
            return "redirect:/admin/employees";
        }
        existing.setHoTen(nhanVien.getHoTen());
        existing.setDiaChi(nhanVien.getDiaChi());
        existing.setSoDienThoai(nhanVien.getSoDienThoai());
        if (chucVuId != null) {
            chucVuRepository.findById(chucVuId).ifPresent(existing::setChucVu);
        }
        // handle account
        try {
            if (tenDangNhap != null && !tenDangNhap.isBlank()) {
                if (existing.getTaiKhoan() == null) {
                    if (matKhau == null || matKhau.isBlank()) {
                        throw new IllegalArgumentException("Password required when creating account");
                    }
                    TaiKhoan tk = new TaiKhoan();
                    tk.setTenDangNhap(tenDangNhap);
                    tk.setMatKhau(matKhau);
                    try {
                        Role r = (role == null || role.isBlank()) ? Role.NHANVIEN : Role.valueOf(role);
                        tk.setQuyenHan(r);
                    } catch (IllegalArgumentException ex) {
                        tk.setQuyenHan(Role.NHANVIEN);
                    }
                    tk.setEnabled(true);
                    TaiKhoan saved = taiKhoanService.save(tk);
                    existing.setTaiKhoan(saved);
                } else {
                    TaiKhoan tk = existing.getTaiKhoan();
                    tk.setTenDangNhap(tenDangNhap);
                    if (matKhau != null && !matKhau.isBlank()) tk.setMatKhau(matKhau);
                    try {
                        Role r = (role == null || role.isBlank()) ? tk.getQuyenHan() : Role.valueOf(role);
                        tk.setQuyenHan(r);
                    } catch (IllegalArgumentException ex) {
                    }
                    taiKhoanService.save(tk);
                }
            }
            nhanVienService.save(existing);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thành công");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/employees/" + id + "/edit";
        }
        return "redirect:/admin/employees";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        nhanVienService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xóa (nếu tồn tại)");
        return "redirect:/admin/employees";
    }
}


