package com.example.demo.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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


