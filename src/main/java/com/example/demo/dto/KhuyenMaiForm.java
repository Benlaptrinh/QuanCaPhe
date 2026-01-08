package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class KhuyenMaiForm {
    private String tenKhuyenMai;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer giaTriGiam;
}


