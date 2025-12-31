package com.example.demo.repository;

import com.example.demo.entity.ChiTietHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ChiTietHoaDonRepository extends JpaRepository<ChiTietHoaDon, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietHoaDon c WHERE c.hoaDon.maHoaDon = :hoaDonId")
    void deleteByHoaDonId(Long hoaDonId);
}


