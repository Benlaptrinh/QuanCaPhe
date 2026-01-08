package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "chi_tiet_thuc_don")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietThucDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_thuc_don")
    private ThucDon thucDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hang_hoa")
    private HangHoa hangHoa;

    @Column(precision = 13, scale = 3)
    private BigDecimal khoiLuong;
}


