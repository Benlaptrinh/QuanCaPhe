package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "don_xuat")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DonXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long maDonXuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_hang_hoa")
    private HangHoa hangHoa;

    private Integer soLuong;

    private LocalDateTime ngayXuat;
}


