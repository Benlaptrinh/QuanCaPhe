package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "chuc_vu")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChucVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @Column(name = "ma_chuc_vu")
    private Long maChucVu;

    @Column(name = "ten_chuc_vu", nullable = false)
    private String tenChucVu;

    @Column(precision = 13, scale = 2)
    private BigDecimal luong;
}


