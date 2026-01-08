package com.example.demo.controller;

import com.example.demo.dto.ReportFilterDTO;
import com.example.demo.dto.ReportRowDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/report")
public class ReportController {

    private String sidebar = "fragments/sidebar-admin";

    private String usernameFromAuth(Authentication auth) {
        return auth == null ? null : auth.getName();
    }

    private List<ReportRowDTO> mockThuChi() {
        return List.of(
            new ReportRowDTO(
                LocalDate.of(2024, 12, 23),
                new BigDecimal("500000"),
                new BigDecimal("1000000")
            ),
            new ReportRowDTO(
                LocalDate.of(2024, 12, 24),
                new BigDecimal("1000000"),
                new BigDecimal("200000")
            )
        );
    }

    @GetMapping
    public String showReportPage(Model model, Authentication auth) {
        java.time.LocalDate now = java.time.LocalDate.now();

        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setFromDate(now.withDayOfMonth(1));
        filter.setToDate(now);

        model.addAttribute("filter", filter);

        // MOCK DATA mặc định (đầu tháng -> hôm nay)
        model.addAttribute("reportData", mockThuChi());

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
        String error = null;
        if (filter.getFromDate() == null || filter.getToDate() == null) {
            error = "Vui lòng chọn đầy đủ ngày";
        } else if (filter.getFromDate().isAfter(filter.getToDate())) {
            error = "Từ ngày không được sau đến ngày";
        }

        if (error != null) {
            model.addAttribute("error", error);
            model.addAttribute("reportData", List.of());
        } else {
            // 👉 MÓC DỮ LIỆU GIẢ
            model.addAttribute("reportData", mockThuChi());
        }

        model.addAttribute("filter", filter);
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/report/index");

        return "layout/base";
    }
}
