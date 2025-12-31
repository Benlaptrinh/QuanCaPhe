package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/staff")
public class StaffPagesController {

    private String sidebar = "fragments/sidebar-staff";

    private String usernameFromAuth(Authentication auth) {
        return auth == null ? "anonymous" : auth.getName();
    }

    @GetMapping("/sales")
    public String sales(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "staff/sales");
        return "layout/base";
    }
}


