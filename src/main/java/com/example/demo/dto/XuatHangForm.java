package com.example.demo.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class XuatHangForm {
    private Long hangHoaId;
    private Integer soLuong;
    private LocalDateTime ngayXuat;
}


