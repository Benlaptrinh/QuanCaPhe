package com.example.demo.controller;

import com.example.demo.entity.ThietBi;
import com.example.demo.service.ThietBiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/equipment")
public class AdminEquipmentController {

    private final ThietBiService thietBiService;

    public AdminEquipmentController(ThietBiService thietBiService) {
        this.thietBiService = thietBiService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("equipments", thietBiService.findAll());
        return "admin/equipment";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("thietBi", new ThietBi());
        return "admin/equipment_create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute ThietBi thietBi, RedirectAttributes ra) {
        thietBiService.save(thietBi);
        ra.addFlashAttribute("success", "Thêm thiết bị thành công");
        return "redirect:/admin/equipment";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        thietBiService.findById(id).ifPresent(thietBi -> model.addAttribute("thietBi", thietBi));
        return "admin/equipment_edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute ThietBi thietBi, RedirectAttributes ra) {
        thietBi.setMaThietBi(id);
        thietBiService.save(thietBi);
        ra.addFlashAttribute("success", "Cập nhật thiết bị thành công");
        return "redirect:/admin/equipment";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        thietBiService.deleteById(id);
        ra.addFlashAttribute("success", "Xóa thiết bị thành công");
        return "redirect:/admin/equipment";
    }
}
