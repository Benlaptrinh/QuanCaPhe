package com.example.demo.dto;

import java.time.LocalDate;

public class ReportRowDTO {

    private LocalDate ngay;
    private Long thu;
    private Long chi;

    public ReportRowDTO(LocalDate ngay, Long thu, Long chi) {
        this.ngay = ngay;
        this.thu = thu == null ? 0L : thu;
        this.chi = chi == null ? 0L : chi;
    }

    // Hibernate / JPQL may pass java.sql.Date and BigDecimal/Number for aggregated results.
    public ReportRowDTO(java.sql.Date sqlDate, java.math.BigDecimal thu, Number chi) {
        this(sqlDate != null ? sqlDate.toLocalDate() : null,
             thu != null ? thu.longValue() : 0L,
             chi != null ? chi.longValue() : 0L);
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public Long getThu() {
        return thu;
    }

    public Long getChi() {
        return chi;
    }
}
