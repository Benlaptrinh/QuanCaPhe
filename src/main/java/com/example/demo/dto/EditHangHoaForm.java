package com.example.demo.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class EditHangHoaForm {
    private Long id;
    private String tenHangHoa;
    private Integer soLuong;
    private Long donViTinhId;
    private BigDecimal donGia;
}


