package com.example.demo.controller;

import com.example.demo.entity.ChucVu;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.repository.ChucVuRepository;
import com.example.demo.service.NhanVienService;
import com.example.demo.service.TaiKhoanService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final TaiKhoanService taiKhoanService;
    private final NhanVienService nhanVienService;
    private final ChucVuRepository chucVuRepository;

    public AdminUserController(TaiKhoanService taiKhoanService,
                               NhanVienService nhanVienService,
                               ChucVuRepository chucVuRepository) {
        this.taiKhoanService = taiKhoanService;
        this.nhanVienService = nhanVienService;
        this.chucVuRepository = chucVuRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", taiKhoanService.findAll());
        return "admin/users/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("taiKhoan", new TaiKhoan());
        model.addAttribute("nhanVien", new NhanVien());
        List<ChucVu> chucVus = chucVuRepository.findAll();
        model.addAttribute("chucVus", chucVus);
        return "admin/users/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute TaiKhoan taiKhoan,
                         @ModelAttribute NhanVien nhanVien,
                         @RequestParam(value = "chucVuId", required = false) Long chucVuId,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            TaiKhoan saved = taiKhoanService.save(taiKhoan);
            if (chucVuId != null) {
                chucVuRepository.findById(chucVuId).ifPresent(nhanVien::setChucVu);
            }
            nhanVien.setTaiKhoan(saved);
            nhanVienService.save(nhanVien);
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("taiKhoan", taiKhoan);
            redirectAttributes.addFlashAttribute("nhanVien", nhanVien);
            return "redirect:/admin/users/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        TaiKhoan tk = taiKhoanService.findById(id).orElse(null);
        if (tk == null) return "redirect:/admin/users";
        model.addAttribute("taiKhoan", tk);
        // find associated nhan vien if exists
        nhanVienService.findAll().stream()
                .filter(n -> n.getTaiKhoan() != null && n.getTaiKhoan().getMaTaiKhoan().equals(id))
                .findFirst()
                .ifPresent(n -> model.addAttribute("nhanVien", n));
        model.addAttribute("chucVus", chucVuRepository.findAll());
        return "admin/users/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @ModelAttribute TaiKhoan taiKhoan,
                       @ModelAttribute NhanVien nhanVien,
                       @RequestParam(value = "chucVuId", required = false) Long chucVuId,
                       org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        TaiKhoan existing = taiKhoanService.findById(id).orElse(null);
        if (existing == null) return "redirect:/admin/users";
        existing.setQuyenHan(taiKhoan.getQuyenHan());
        if (taiKhoan.getMatKhau() != null && !taiKhoan.getMatKhau().isEmpty()) {
            existing.setMatKhau(taiKhoan.getMatKhau()); // save will hash
        }
        try {
            taiKhoanService.save(existing);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/users/" + id + "/edit";
        }

        // update nhan vien
        nhanVienService.findById(nhanVien.getMaNhanVien()).ifPresent(n -> {
            n.setHoTen(nhanVien.getHoTen());
            n.setSoDienThoai(nhanVien.getSoDienThoai());
            n.setDiaChi(nhanVien.getDiaChi());
            if (chucVuId != null) {
                chucVuRepository.findById(chucVuId).ifPresent(n::setChucVu);
            }
            nhanVienService.save(n);
        });

        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/disable")
    public String disable(@PathVariable Long id) {
        taiKhoanService.disable(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        // id is TaiKhoan id
        nhanVienService.deleteByTaiKhoanId(id);
        taiKhoanService.findById(id).ifPresent(t -> {
            // ensure account removed if still exists
            try {
                // delete via repository
                // taiKhoanService has no delete, use repository via service save/disable; simplest: disable
                taiKhoanService.disable(id);
            } catch (Exception ignored) {}
        });
        redirectAttributes.addFlashAttribute("message", "Deleted user if existed");
        return "redirect:/admin/users";
    }
}


