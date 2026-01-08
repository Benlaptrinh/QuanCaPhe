package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ThuChiDTO {
    private LocalDate ngay;
    private BigDecimal thu;
    private BigDecimal chi;
}


