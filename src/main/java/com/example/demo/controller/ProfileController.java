package com.example.demo.controller;

import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.service.NhanVienService;
import com.example.demo.service.TaiKhoanService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final TaiKhoanService taiKhoanService;
    private final NhanVienService nhanVienService;

    public ProfileController(TaiKhoanService taiKhoanService, NhanVienService nhanVienService) {
        this.taiKhoanService = taiKhoanService;
        this.nhanVienService = nhanVienService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication auth) {
        String username = auth.getName();
        TaiKhoan tk = taiKhoanService.findByUsername(username).orElse(null);
        if (tk == null) return "redirect:/login";
        NhanVien nv = nhanVienService.findByTaiKhoanId(tk.getMaTaiKhoan()).orElse(null);
        model.addAttribute("taiKhoan", tk);
        model.addAttribute("nhanVien", nv == null ? new NhanVien() : nv);
        
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("sidebarFragment", isAdmin ? "fragments/sidebar-admin" : "fragments/sidebar-staff");
        model.addAttribute("contentFragment", "profile/view");
        model.addAttribute("username", username);
        return "layout/base";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model, Authentication auth) {
        String username = auth.getName();
        TaiKhoan tk = taiKhoanService.findByUsername(username).orElse(null);
        if (tk == null) return "redirect:/login";
        NhanVien nv = nhanVienService.findByTaiKhoanId(tk.getMaTaiKhoan()).orElse(new NhanVien());
        model.addAttribute("taiKhoan", tk);
        model.addAttribute("nhanVien", nv);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        model.addAttribute("sidebarFragment", isAdmin ? "fragments/sidebar-admin" : "fragments/sidebar-staff");
        model.addAttribute("contentFragment", "profile/edit");
        model.addAttribute("username", username);
        return "layout/base";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Authentication auth,
                                @RequestParam String hoTen,
                                @RequestParam String diaChi,
                                @RequestParam String soDienThoai,
                                @RequestParam(required = false) String anh) {
        String username = auth.getName();
        TaiKhoan tk = taiKhoanService.findByUsername(username).orElse(null);
        if (tk == null) return "redirect:/login";
        NhanVien nv = nhanVienService.findByTaiKhoanId(tk.getMaTaiKhoan()).orElse(new NhanVien());
        nv.setHoTen(hoTen);
        nv.setDiaChi(diaChi);
        nv.setSoDienThoai(soDienThoai);
        nv.setTaiKhoan(tk);
        nhanVienService.save(nv);
        if (anh != null) {
            tk.setAnh(anh);
            taiKhoanService.save(tk);
        }
        return "redirect:/profile";
    }
}


