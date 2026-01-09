package com.example.demo.controller;

import com.example.demo.dto.KhuyenMaiForm;
import com.example.demo.service.KhuyenMaiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AdminMarketingController
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
@RequestMapping("/admin/marketing")
public class AdminMarketingController {

    private final KhuyenMaiService khuyenMaiService;

    /**
     * Creates AdminMarketingController.
     *
     * @param khuyenMaiService khuyenMaiService
     */
    public AdminMarketingController(KhuyenMaiService khuyenMaiService) {
        this.khuyenMaiService = khuyenMaiService;
    }

    /**
     * Show add form.
     *
     * @param model model
     * @return result
     */
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

    /**
     * Add khuyen mai.
     *
     * @param khuyenMaiForm khuyenMaiForm
     * @param redirectAttributes redirectAttributes
     * @param model model
     * @return result
     */
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

    /**
     * Show edit form.
     *
     * @param id id
     * @param model model
     * @param ra ra
     * @return result
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            KhuyenMaiForm form = khuyenMaiService.getFormById(id);
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

    /**
     * Update khuyen mai.
     *
     * @param id id
     * @param khuyenMaiForm khuyenMaiForm
     * @param redirectAttributes redirectAttributes
     * @param model model
     * @return result
     */
    @PostMapping("/edit/{id}")
    public String updateKhuyenMai(@PathVariable Long id,
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

    /**
     * Delete khuyen mai.
     *
     * @param id id
     * @param redirectAttributes redirectAttributes
     * @return result
     */
    @GetMapping("/delete/{id}")
    public String deleteKhuyenMai(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            khuyenMaiService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/marketing";
    }
}

