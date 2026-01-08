package com.example.demo.controller;

import com.example.demo.entity.HoaDon;
import com.example.demo.service.SalesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class InvoiceController {

    private final SalesService salesService;

    public InvoiceController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping("/invoice/{hoaDonId}")
    public String printInvoice(@PathVariable("hoaDonId") Long hoaDonId, Model model) {
        HoaDon hoaDon = salesService.findInvoiceById(hoaDonId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("details", hoaDon.getChiTietHoaDons());
        return "sales/invoice-print";
    }
}


