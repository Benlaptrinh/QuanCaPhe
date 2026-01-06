package com.example.demo.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class XuatHangForm {
    private Long hangHoaId;
    private Integer soLuong;
    private LocalDateTime ngayXuat;
}


