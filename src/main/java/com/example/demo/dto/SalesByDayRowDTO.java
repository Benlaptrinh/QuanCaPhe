package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SalesByDayRowDTO {

    private LocalDate ngay;
    private Long soHoaDon;
    private BigDecimal doanhThu;

    public SalesByDayRowDTO(LocalDate ngay, Long soHoaDon, BigDecimal doanhThu) {
        this.ngay = ngay;
        this.soHoaDon = soHoaDon == null ? 0L : soHoaDon;
        this.doanhThu = doanhThu == null ? BigDecimal.ZERO : doanhThu;
    }

    public LocalDate getNgay() {
        return ngay;
    }

    public Long getSoHoaDon() {
        return soHoaDon;
    }

    public BigDecimal getDoanhThu() {
        return doanhThu;
    }
}


