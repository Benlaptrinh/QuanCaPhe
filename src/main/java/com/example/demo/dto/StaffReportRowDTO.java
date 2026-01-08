package com.example.demo.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StaffReportRowDTO {

    private String trangThai;
    private Long soLuong;
    private BigDecimal tongLuong;

    

    public String getTrangThai() {
        return trangThai;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public BigDecimal getTongLuong() {
        return tongLuong;
    }
}


