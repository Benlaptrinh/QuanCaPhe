package com.example.demo.repository;

import com.example.demo.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import com.example.demo.entity.Ban;
import com.example.demo.enums.TrangThaiHoaDon;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    @Query("select h from HoaDon h where h.ban.maBan = :maBan and h.trangThai = 'MOI_TAO'")
    Optional<HoaDon> findChuaThanhToanByBan(@Param("maBan") Long maBan);

    boolean existsByBanAndTrangThai(Ban ban, TrangThaiHoaDon trangThai);
    List<HoaDon> findByNgayThanhToanBetween(LocalDateTime from, LocalDateTime to);
    @Query("""
        SELECT h
        FROM HoaDon h
        WHERE h.ngayThanhToan BETWEEN :from AND :to
          AND h.trangThai = com.example.demo.enums.TrangThaiHoaDon.DA_THANH_TOAN
    """)
    List<HoaDon> findHoaDonDaThanhToan(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
    
    @Query(value = """
        SELECT
            DATE(h.ngay_gio_tao) AS ngay,
            SUM(h.tong_tien) AS thu,
            0 AS chi
        FROM hoa_don h
        WHERE h.ngay_gio_tao BETWEEN :from AND :to
          AND h.trang_thai = 'DA_THANH_TOAN'
        GROUP BY DATE(h.ngay_gio_tao)
        ORDER BY DATE(h.ngay_gio_tao)
    """, nativeQuery = true)
    List<Object[]> thongKeThuRaw(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
    
    @Query(value = """
        SELECT 
            DATE(h.ngay_thanh_toan) AS ngay,
            COUNT(h.ma_hoa_don) AS soHoaDon,
            COALESCE(SUM(h.tong_tien), 0) AS doanhThu
        FROM hoa_don h
        WHERE h.trang_thai = 'DA_THANH_TOAN'
          AND h.ngay_thanh_toan BETWEEN :from AND :to
        GROUP BY DATE(h.ngay_thanh_toan)
        ORDER BY DATE(h.ngay_thanh_toan)
    """, nativeQuery = true)
    List<Object[]> thongKeBanHangTheoNgayRaw(
        @Param("from") LocalDateTime from,
        @Param("to") LocalDateTime to
    );
}


