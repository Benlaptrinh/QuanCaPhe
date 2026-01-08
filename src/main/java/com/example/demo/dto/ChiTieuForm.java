package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ChiTieuForm {
    private LocalDate ngayChi;
    private String tenKhoanChi;
    private BigDecimal soTien;
}


