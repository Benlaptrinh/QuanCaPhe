package com.example.demo.repository;

import com.example.demo.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HoaDonRepository extends JpaRepository<HoaDon, Long> {
    @Query("select h from HoaDon h where h.ban.maBan = :maBan and h.trangThai = 'MOI_TAO'")
    Optional<HoaDon> findChuaThanhToanByBan(@Param("maBan") Long maBan);
}


