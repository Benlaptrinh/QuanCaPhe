package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaffController {

    @GetMapping("/staff/home")
    public String home(org.springframework.ui.Model model, org.springframework.security.core.Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("sidebarFragment", "fragments/sidebar-staff");
        model.addAttribute("contentFragment", "staff/home");
        return "layout/base";
    }
}


