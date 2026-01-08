package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChiTieuForm {
    private LocalDate ngayChi;
    private String tenKhoanChi;
    private BigDecimal soTien;
}


