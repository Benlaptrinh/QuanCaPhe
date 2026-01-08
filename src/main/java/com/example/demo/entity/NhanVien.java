package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nhan_vien")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long maNhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_chuc_vu")
    private ChucVu chucVu;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_tai_khoan", unique = true)
    private TaiKhoan taiKhoan;

    @Column(nullable = false)
    private String hoTen;

    private String soDienThoai;
    private String diaChi;
    
    @Column(precision = 13, scale = 2)
    private java.math.BigDecimal luong;

    @Column(nullable = false)
private Boolean enabled = true;

}


