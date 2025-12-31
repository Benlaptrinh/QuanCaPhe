package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "khuyen_mai")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maKhuyenMai;

    private String tenKhuyenMai;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    @Column(precision = 13, scale = 2)
    private BigDecimal giaTriGiam;

    private String trangThai;
    private String moTa;
}


