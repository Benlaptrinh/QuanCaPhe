package com.example.demo.repository;

import com.example.demo.entity.ChiTietDatBan;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.entity.Ban;

public interface ChiTietDatBanRepository extends JpaRepository<ChiTietDatBan, Long> {
    void deleteByBan(Ban ban);
}


