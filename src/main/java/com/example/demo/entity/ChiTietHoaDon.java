package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_hoa_don")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChiTietHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hoa_don")
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thuc_don")
    private ThucDon thucDon;

    private Integer soLuong;

    @Column(precision = 13, scale = 2)
    private BigDecimal giaTaiThoiDiemBan;

    @Column(precision = 13, scale = 2)
    private BigDecimal thanhTien;
}


