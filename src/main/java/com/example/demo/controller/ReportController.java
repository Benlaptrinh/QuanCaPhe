package com.example.demo.controller;

import com.example.demo.dto.ReportFilterDTO;
import com.example.demo.dto.ReportRowDTO;
import com.example.demo.service.ReportService;
import com.example.demo.dto.StaffReportRowDTO;
import com.example.demo.dto.SalesByDayRowDTO;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin/report")
public class ReportController {

    private String sidebar = "fragments/sidebar-admin";
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    private String usernameFromAuth(Authentication auth) {
        return auth == null ? null : auth.getName();
    }

    

    @GetMapping
    public String showReportPage(Model model, Authentication auth) {
        java.time.LocalDate now = java.time.LocalDate.now();

        ReportFilterDTO filter = new ReportFilterDTO();
        filter.setFromDate(now.withDayOfMonth(1));
        filter.setToDate(now);

        model.addAttribute("filter", filter);

        // Lấy dữ liệu thực từ service (hiện service sẽ truy vấn repository)
        model.addAttribute("reportData", reportService.thongKeThuChi(filter.getFromDate(), filter.getToDate()));
        // sales by day
        model.addAttribute("salesByDay", reportService.reportSalesByDay(filter.getFromDate(), filter.getToDate()));
        // staff report
        model.addAttribute("staffReport", reportService.thongKeNhanVien());

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
            model.addAttribute("salesByDay", List.of());
            model.addAttribute("staffReport", List.of());
        } else {
            model.addAttribute("reportData", reportService.thongKeThuChi(filter.getFromDate(), filter.getToDate()));
            model.addAttribute("salesByDay", reportService.reportSalesByDay(filter.getFromDate(), filter.getToDate()));
            model.addAttribute("staffReport", reportService.thongKeNhanVien());
        }

        model.addAttribute("filter", filter);
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/report/index");

        return "layout/base";
    }
}
