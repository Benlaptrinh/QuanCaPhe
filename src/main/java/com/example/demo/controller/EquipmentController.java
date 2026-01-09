package com.example.demo.controller;

import java.util.Optional;

import com.example.demo.entity.ThietBi;
import com.example.demo.service.ThietBiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * EquipmentController
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
@RequestMapping("/admin/equipment")
public class EquipmentController {

    private final ThietBiService thietBiService;

    /**
     * Creates EquipmentController.
     *
     * @param thietBiService thietBiService
     */
    public EquipmentController(ThietBiService thietBiService) {
        this.thietBiService = thietBiService;
    }

    /**
     * Create.
     *
     * @param thietBi thietBi
     * @param ra ra
     * @return result
     */
    @PostMapping
    public String create(@ModelAttribute ThietBi thietBi, RedirectAttributes ra) {
        thietBiService.save(thietBi);
        ra.addFlashAttribute("success", "Thêm thiết bị thành công");
        return "redirect:/admin/equipment";
    }

    /**
     * Edit form.
     *
     * @param id id
     * @param model model
     * @param ra ra
     * @return result
     */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        Optional<ThietBi> opt = thietBiService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Thiết bị không tồn tại");
            return "redirect:/admin/equipment";
        }
        ra.addFlashAttribute("thietBi", opt.get());
        return "redirect:/admin/equipment";
    }

    /**
     * Update.
     *
     * @param id id
     * @param thietBi thietBi
     * @param ra ra
     * @return result
     */
    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute ThietBi thietBi, RedirectAttributes ra) {
        Optional<ThietBi> opt = thietBiService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Thiết bị không tồn tại");
            return "redirect:/admin/equipment";
        }
        ThietBi existing = opt.get();
        existing.setTenThietBi(thietBi.getTenThietBi());
        existing.setSoLuong(thietBi.getSoLuong());
        existing.setDonGiaMua(thietBi.getDonGiaMua());
        existing.setNgayMua(thietBi.getNgayMua());
        existing.setGhiChu(thietBi.getGhiChu());
        thietBiService.save(existing);
        ra.addFlashAttribute("success", "Cập nhật thiết bị thành công");
        return "redirect:/admin/equipment";
    }

    /**
     * Delete.
     *
     * @param id id
     * @param ra ra
     * @return result
     */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        thietBiService.deleteById(id);
        ra.addFlashAttribute("success", "Xóa thiết bị thành công");
        return "redirect:/admin/equipment";
    }
}

