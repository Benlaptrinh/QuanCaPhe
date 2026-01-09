package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * ChiTieuForm
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
@Getter
@Setter
public class ChiTieuForm {
    private LocalDate ngayChi;
    private String tenKhoanChi;
    private BigDecimal soTien;
}


