package com.example.demo.controller;

import com.example.demo.dto.KhuyenMaiForm;
import com.example.demo.service.KhuyenMaiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/marketing")
public class AdminMarketingController {

    private final KhuyenMaiService khuyenMaiService;

    public AdminMarketingController(KhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        if (!model.containsAttribute("khuyenMaiForm")) {
            model.addAttribute("khuyenMaiForm", new KhuyenMaiForm());
        }
        model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
        model.addAttribute("contentFragment", "admin/marketing/add");
        model.addAttribute("activeMenu", "marketing");
        return "layout/base";
    }

    @PostMapping("/add")
    public String addKhuyenMai(KhuyenMaiForm khuyenMaiForm, RedirectAttributes redirectAttributes, Model model) {
        try {
            khuyenMaiService.createKhuyenMai(khuyenMaiForm);
            redirectAttributes.addFlashAttribute("success", "Thêm khuyến mãi thành công");
            return "redirect:/admin/marketing";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("khuyenMaiForm", khuyenMaiForm);
            model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
            model.addAttribute("contentFragment", "admin/marketing/add");
            model.addAttribute("activeMenu", "marketing");
            return "layout/base";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@org.springframework.web.bind.annotation.PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            com.example.demo.dto.KhuyenMaiForm form = khuyenMaiService.getFormById(id);
            model.addAttribute("khuyenMaiForm", form);
            model.addAttribute("khuyenMaiId", id);
            model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
            model.addAttribute("contentFragment", "admin/marketing/edit");
            model.addAttribute("activeMenu", "marketing");
            return "layout/base";
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/marketing";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateKhuyenMai(@org.springframework.web.bind.annotation.PathVariable Long id,
                                  KhuyenMaiForm khuyenMaiForm,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        try {
            khuyenMaiService.updateKhuyenMai(id, khuyenMaiForm);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khuyến mãi thành công");
            return "redirect:/admin/marketing";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("khuyenMaiForm", khuyenMaiForm);
            model.addAttribute("khuyenMaiId", id);
            model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
            model.addAttribute("contentFragment", "admin/marketing/edit");
            model.addAttribute("activeMenu", "marketing");
            return "layout/base";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteKhuyenMai(@org.springframework.web.bind.annotation.PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            khuyenMaiService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/marketing";
    }
}


