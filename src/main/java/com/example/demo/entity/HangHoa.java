package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hang_hoa")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class HangHoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maHangHoa;

    private String tenHangHoa;

    private Integer soLuong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_don_vi_tinh")
    private DonViTinh donViTinh;

    @Column(precision = 13, scale = 2)
    private BigDecimal donGia;
}


