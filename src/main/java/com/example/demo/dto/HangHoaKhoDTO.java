package com.example.demo.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HangHoaKhoDTO {

    private Long maHangHoa;
    private String tenHangHoa;
    private Integer soLuong;
    private String donVi;
    private BigDecimal donGia;

    private LocalDateTime ngayNhapGanNhat;
    private LocalDateTime ngayXuatGanNhat;
}


