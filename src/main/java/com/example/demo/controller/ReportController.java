package com.example.demo.controller;

import com.example.demo.dto.ReportFilterDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/report")
public class ReportController {

    @GetMapping
    public String showReportPage(Model model) {
        ReportFilterDTO filter = new ReportFilterDTO();
        model.addAttribute("filter", filter);
        model.addAttribute("reportData", List.of()); // tạm thời rỗng
        // ...existing code...
        return "layout/admin-layout";
    }

    @PostMapping
    public String viewReport(
            @ModelAttribute("filter") ReportFilterDTO filter,
            Model model
    ) {
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            model.addAttribute("error", "Vui lòng chọn đầy đủ ngày");
        } else if (filter.getFromDate().isAfter(filter.getToDate())) {
            model.addAttribute("error", "Từ ngày không được sau đến ngày");
        } else {
            model.addAttribute("reportData", List.of());
        }
        model.addAttribute("filter", filter);
        // ...existing code...
        return "layout/admin-layout";
    }
}
