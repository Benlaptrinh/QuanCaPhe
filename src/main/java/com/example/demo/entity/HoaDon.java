package com.example.demo.entity;

import com.example.demo.enums.TrangThaiHoaDon;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long maHoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ban")
    private Ban ban;

    @Column(name = "ngay_gio_tao")
    private LocalDateTime ngayGioTao;

    @Column(precision = 13, scale = 2)
    private BigDecimal tongTien;

    @Enumerated(EnumType.STRING)
    private TrangThaiHoaDon trangThai;
    
    private LocalDateTime ngayThanhToan;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<ChiTietHoaDon> chiTietHoaDons;
}


