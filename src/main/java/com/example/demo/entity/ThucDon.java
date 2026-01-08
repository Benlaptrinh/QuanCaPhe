package com.example.demo.entity;

import java.math.BigDecimal;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "thuc_don")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ThucDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long maThucDon;

    private String tenMon;

    @Column(precision = 13, scale = 2)
    private BigDecimal giaHienTai;

    private String loaiMon;
}


