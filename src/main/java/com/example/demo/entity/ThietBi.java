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

    private String tenThietBi;
    private Integer soLuong;

    @Column(precision = 13, scale = 2)
    private BigDecimal donGiaMua;

    private LocalDate ngayMua;
    private String ghiChu;
}


