package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "don_vi_tinh")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonViTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDonViTinh;

    private String tenDonVi;
}


