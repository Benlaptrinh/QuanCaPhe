package com.example.demo.controller;

import java.util.List;

import com.example.demo.controller.base.BaseController;
import com.example.demo.dto.NhanVienForm;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.enums.Role;
import com.example.demo.repository.ChucVuRepository;
import com.example.demo.service.NhanVienService;
import com.example.demo.service.TaiKhoanService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AdminEmployeesController
 *
 * Version 1.0
 *
 * Date: 09-01-2026
 *
 * Copyright
 *
 * Modification Logs:
 * DATE        AUTHOR      DESCRIPTION
 * -----------------------------------
 * 09-01-2026  Việt    Create
 */
@Controller
@RequestMapping("/admin/employees")
public class AdminEmployeesController extends BaseController {

    private final NhanVienService nhanVienService;
    private final TaiKhoanService taiKhoanService;
    private final ChucVuRepository chucVuRepository;

    /**
     * Creates AdminEmployeesController.
     *
     * @param nhanVienService nhanVienService
     * @param taiKhoanService taiKhoanService
     * @param chucVuRepository chucVuRepository
     */
    public AdminEmployeesController(NhanVienService nhanVienService, TaiKhoanService taiKhoanService, ChucVuRepository chucVuRepository) {
        this.nhanVienService = nhanVienService;
        this.taiKhoanService = taiKhoanService;
        this.chucVuRepository = chucVuRepository;
    }

    /**
     * List.
     *
     * @param q q
     * @param model model
     * @param auth auth
     * @return result
     */
    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q, Model model, Authentication auth) {
        List<NhanVien> list;
        if (q != null && !q.isBlank()) {
            list = nhanVienService.findByHoTenContaining(q);
        } else {
            list = nhanVienService.findAll();
        }
        model.addAttribute("nhanViens", list);
        setupAdminLayout(model, "admin/employees_list", auth);
        return "layout/base";
    }

    /**
     * Create form.
     *
     * @param model model
     * @param auth auth
     * @return result
     */
    @GetMapping("/create")
    public String createForm(Model model, Authentication auth) {
        model.addAttribute("form", new NhanVienForm());
        model.addAttribute("chucVus", chucVuRepository.findAll());
        setupAdminLayout(model, "admin/employees_create", auth);
        return "layout/base";
    }

    /**
     * Create.
     *
     * @param nhanVien nhanVien
     * @param chucVuId chucVuId
     * @param tenDangNhap tenDangNhap
     * @param matKhau matKhau
     * @param role role
     * @param redirectAttributes redirectAttributes
     * @return result
     */
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("form") NhanVienForm form,
                         BindingResult bindingResult,
                         Model model,
                         Authentication auth,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("chucVus", chucVuRepository.findAll());
            setupAdminLayout(model, "admin/employees_create", auth);
            return "layout/base";
        }
        NhanVien nhanVien = new NhanVien();
        nhanVien.setHoTen(form.getHoTen());
        nhanVien.setDiaChi(form.getDiaChi());
        nhanVien.setSoDienThoai(form.getSoDienThoai());
        if (form.getChucVuId() != null) {
            chucVuRepository.findById(form.getChucVuId()).ifPresent(nhanVien::setChucVu);
        }
        try {
            if (form.getTenDangNhap() != null && !form.getTenDangNhap().isBlank()) {
                if (form.getMatKhau() == null || form.getMatKhau().isBlank()) {
                    throw new IllegalArgumentException("Password required when creating account");
                }
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(form.getTenDangNhap());
                tk.setMatKhau(form.getMatKhau());
                try {
                    Role r = (form.getRole() == null || form.getRole().isBlank()) ? Role.NHANVIEN : Role.valueOf(form.getRole());
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
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("chucVus", chucVuRepository.findAll());
            setupAdminLayout(model, "admin/employees_create", auth);
            return "layout/base";
        }
    }

    /**
     * Edit form.
     *
     * @param id id
     * @param model model
     * @param auth auth
     * @return result
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication auth) {
        NhanVien nv = nhanVienService.findById(id).orElse(new NhanVien());
        model.addAttribute("form", toForm(nv));
        model.addAttribute("employeeId", id);
        model.addAttribute("chucVus", chucVuRepository.findAll());
        setupAdminLayout(model, "admin/employees_edit", auth);
        return "layout/base";
    }

    /**
     * Edit.
     *
     * @param id id
     * @param nhanVien nhanVien
     * @param chucVuId chucVuId
     * @param tenDangNhap tenDangNhap
     * @param matKhau matKhau
     * @param role role
     * @param redirectAttributes redirectAttributes
     * @return result
     */
    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("form") NhanVienForm form,
                       BindingResult bindingResult,
                       Model model,
                       Authentication auth,
                       RedirectAttributes redirectAttributes) {
        NhanVien existing = nhanVienService.findById(id).orElse(null);
        if (existing == null) {
            redirectAttributes.addFlashAttribute("error", "Nhân viên không tồn tại");
            return "redirect:/admin/employees";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("employeeId", id);
            model.addAttribute("chucVus", chucVuRepository.findAll());
            setupAdminLayout(model, "admin/employees_edit", auth);
            return "layout/base";
        }
        existing.setHoTen(form.getHoTen());
        existing.setDiaChi(form.getDiaChi());
        existing.setSoDienThoai(form.getSoDienThoai());
        if (form.getChucVuId() != null) {
            chucVuRepository.findById(form.getChucVuId()).ifPresent(existing::setChucVu);
        }
        
        try {
            if (form.getTenDangNhap() != null && !form.getTenDangNhap().isBlank()) {
                if (existing.getTaiKhoan() == null) {
                    if (form.getMatKhau() == null || form.getMatKhau().isBlank()) {
                        throw new IllegalArgumentException("Password required when creating account");
                    }
                    TaiKhoan tk = new TaiKhoan();
                    tk.setTenDangNhap(form.getTenDangNhap());
                    tk.setMatKhau(form.getMatKhau());
                    try {
                        Role r = (form.getRole() == null || form.getRole().isBlank()) ? Role.NHANVIEN : Role.valueOf(form.getRole());
                        tk.setQuyenHan(r);
                    } catch (IllegalArgumentException ex) {
                        tk.setQuyenHan(Role.NHANVIEN);
                    }
                    tk.setEnabled(true);
                    TaiKhoan saved = taiKhoanService.save(tk);
                    existing.setTaiKhoan(saved);
                } else {
                    TaiKhoan tk = existing.getTaiKhoan();
                    tk.setTenDangNhap(form.getTenDangNhap());
                    if (form.getMatKhau() != null && !form.getMatKhau().isBlank()) tk.setMatKhau(form.getMatKhau());
                    try {
                        Role r = (form.getRole() == null || form.getRole().isBlank()) ? tk.getQuyenHan() : Role.valueOf(form.getRole());
                        tk.setQuyenHan(r);
                    } catch (IllegalArgumentException ex) {
                    }
                    taiKhoanService.save(tk);
                }
            }
            nhanVienService.save(existing);
            redirectAttributes.addFlashAttribute("message", "Cập nhật thành công");
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("employeeId", id);
            model.addAttribute("chucVus", chucVuRepository.findAll());
            setupAdminLayout(model, "admin/employees_edit", auth);
            return "layout/base";
        }
        return "redirect:/admin/employees";
    }

    private NhanVienForm toForm(NhanVien nv) {
        NhanVienForm form = new NhanVienForm();
        form.setHoTen(nv.getHoTen());
        form.setDiaChi(nv.getDiaChi());
        form.setSoDienThoai(nv.getSoDienThoai());
        if (nv.getChucVu() != null) {
            form.setChucVuId(nv.getChucVu().getMaChucVu());
        }
        if (nv.getTaiKhoan() != null) {
            form.setTenDangNhap(nv.getTaiKhoan().getTenDangNhap());
            if (nv.getTaiKhoan().getQuyenHan() != null) {
                form.setRole(nv.getTaiKhoan().getQuyenHan().name());
            }
        }
        return form;
    }

    /**
     * Delete.
     *
     * @param id id
     * @param redirectAttributes redirectAttributes
     * @return result
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        nhanVienService.deleteById(id);
        redirectAttributes.addFlashAttribute("message", "Đã xóa (nếu tồn tại)");
        return "redirect:/admin/employees";
    }
}
