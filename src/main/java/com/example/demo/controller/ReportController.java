package com.example.demo.controller;

import com.example.demo.dto.ReportFilterDTO;
import org.springframework.security.core.Authentication;
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

    private String sidebar = "fragments/sidebar-admin";

    private String usernameFromAuth(Authentication auth) {
        return auth == null ? null : auth.getName();
    }

    @GetMapping
    public String showReportPage(Model model, Authentication auth) {
        ReportFilterDTO filter = new ReportFilterDTO();
        model.addAttribute("filter", filter);
        model.addAttribute("reportData", List.of()); // tạm thời rỗng

        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/report/index");

        return "layout/base";
    }

    @PostMapping
    public String viewReport(
            @ModelAttribute("filter") ReportFilterDTO filter,
            Model model,
            Authentication auth
    ) {
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            model.addAttribute("error", "Vui lòng chọn đầy đủ ngày");
        } else if (filter.getFromDate().isAfter(filter.getToDate())) {
            model.addAttribute("error", "Từ ngày không được sau đến ngày");
        } else {
            model.addAttribute("reportData", List.of());
        }

        model.addAttribute("filter", filter);
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/report/index");

        return "layout/base";
    }
}
