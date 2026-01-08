package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportRowDTO {

    private LocalDate ngay;
    private BigDecimal thu;
    private BigDecimal chi;

    public ReportRowDTO(LocalDate ngay, BigDecimal thu, BigDecimal chi) {
        this.ngay = ngay;
        this.thu = thu;
        this.chi = chi;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public BigDecimal getThu() {
        return thu;
    }

    public BigDecimal getChi() {
        return chi;
    }
}
