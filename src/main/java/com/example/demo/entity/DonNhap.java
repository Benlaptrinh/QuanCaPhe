package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "don_nhap")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDonNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hang_hoa")
    private HangHoa hangHoa;

    private Integer soLuong;

    private LocalDateTime ngayNhap;
}


