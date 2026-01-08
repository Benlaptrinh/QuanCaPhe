package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_dat_ban")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ChiTietDatBan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_ban")
    private Ban ban;

    private String tenKhach;

    private String sdt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_nhan_vien")
    private NhanVien nhanVien;

    private LocalDateTime ngayGioDat;
}


