package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "chi_tieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maChiTieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tai_khoan")
    private TaiKhoan taiKhoan;

    @Column(nullable = false)
    private String tenKhoanChi;

    @Column(precision = 13, scale = 2)
    private BigDecimal soTien;

    private LocalDate ngayChi;
}