package com.example.demo.dto;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.sql.Date;

public class ReportRowDTO {

    private LocalDate ngay;
    private Long thu;
    private Long chi;

    public ReportRowDTO(LocalDate ngay, Long thu, Long chi) {
        this.ngay = ngay;
        this.thu = thu == null ? 0L : thu;
        this.chi = chi == null ? 0L : chi;
    }

    
    public ReportRowDTO(Date sqlDate, BigDecimal thu, Number chi) {
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
