package com.example.demo.controller;

import com.example.demo.entity.HoaDon;
import com.example.demo.service.SalesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * InvoiceController
 *
 * Version 1.0
 *
 * Date: 09-01-2026
 *
 * Copyright
 *
 * Modification Logs:
 * DATE        AUTHOR      DESCRIPTION
 * -----------------------------------
 * 09-01-2026  Việt    Create
 */
@Controller
public class InvoiceController {

    private final SalesService salesService;

    /**
     * Creates InvoiceController.
     *
     * @param salesService salesService
     */
    public InvoiceController(SalesService salesService) {
        this.salesService = salesService;
    }

    /**
     * Print invoice.
     *
     * @param hoaDonId hoaDonId
     * @param model model
     * @return result
     */
    @GetMapping("/invoice/{hoaDonId}")
    public String printInvoice(@PathVariable("hoaDonId") Long hoaDonId, Model model) {
        HoaDon hoaDon = salesService.findInvoiceById(hoaDonId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("details", hoaDon.getChiTietHoaDons());
        return "sales/invoice-print";
    }
}


