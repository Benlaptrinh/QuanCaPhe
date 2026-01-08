package com.example.demo.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HangHoaNhapForm {
    private String tenHangHoa;
    private Integer soLuong;
    private Long donViTinhId;
    private BigDecimal donGia;
    private LocalDateTime ngayNhap;
}


