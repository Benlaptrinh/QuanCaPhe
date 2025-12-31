package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.service.SalesService;

@Controller
@RequestMapping("/admin")
public class AdminPagesController {

    private final SalesService salesService;
    private String sidebar = "fragments/sidebar-admin";

    public AdminPagesController(SalesService salesService) {
        this.salesService = salesService;
    }

    private String usernameFromAuth(Authentication auth) {
        return auth == null ? "anonymous" : auth.getName();
    }

    @GetMapping("/sales")
    public String sales(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "sales/index");
        model.addAttribute("tables", salesService.findAllTables());
        return "layout/base";
    }

    @GetMapping("/equipment")
    public String equipment(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/equipment");
        return "layout/base";
    }

    @GetMapping("/warehouse")
    public String warehouse(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/warehouse");
        return "layout/base";
    }

    @GetMapping("/menu")
    public String menu(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/menu");
        return "layout/base";
    }

    @GetMapping("/marketing")
    public String marketing(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/marketing");
        return "layout/base";
    }

    @GetMapping("/budget")
    public String budget(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/budget");
        return "layout/base";
    }

    @GetMapping("/reports")
    public String reports(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/reports");
        return "layout/base";
    }
}


