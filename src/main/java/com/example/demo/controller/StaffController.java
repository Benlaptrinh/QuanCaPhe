package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

@Controller
public class StaffController {

    @GetMapping("/staff/home")
    public String home(Model model, Authentication auth) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("sidebarFragment", "fragments/sidebar-staff");
        model.addAttribute("contentFragment", "staff/home");
        return "layout/base";
    }
}


