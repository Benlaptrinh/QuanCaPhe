package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@Controller
public class AdminController {

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("sidebarFragment", "fragments/sidebar-admin");
        model.addAttribute("contentFragment", "admin/dashboard");
        return "layout/base";
    }
}


