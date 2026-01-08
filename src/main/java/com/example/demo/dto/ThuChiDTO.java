package com.example.demo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ThuChiDTO {
    private LocalDate ngay;
    private BigDecimal thu;
    private BigDecimal chi;
}


