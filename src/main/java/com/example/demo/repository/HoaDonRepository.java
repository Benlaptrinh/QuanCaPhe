package com.example.demo.repository;

import com.example.demo.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    @Query("select h from HoaDon h where h.ban.maBan = :maBan and h.trangThai = 'MOI_TAO'")
    Optional<HoaDon> findChuaThanhToanByBan(@Param("maBan") Long maBan);

    boolean existsByBanAndTrangThai(com.example.demo.entity.Ban ban, com.example.demo.enums.TrangThaiHoaDon trangThai);
    List<HoaDon> findByNgayThanhToanBetween(LocalDateTime from, LocalDateTime to);
}


