package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "thiet_bi")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maThietBi;

    @Column(name = "ten", nullable = false)
    private String tenThietBi;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia_mua", precision = 13, scale = 2, nullable = false)
    private BigDecimal donGiaMua;

    @Column(name = "ngay_mua", nullable = false)
    private LocalDate ngayMua;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Transient
    public BigDecimal getTongGia() {
        if (donGiaMua == null || soLuong == null) {
            return BigDecimal.ZERO;
        }
        return donGiaMua.multiply(BigDecimal.valueOf(soLuong));
    }
}


