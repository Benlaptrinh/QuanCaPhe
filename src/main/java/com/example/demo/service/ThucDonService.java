package com.example.demo.service;

import com.example.demo.entity.ThucDon;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

public interface ThucDonService {
    List<ThucDon> findAll();
    void create(String tenMon, BigDecimal giaTien);
    void update(Long id, String tenMon, BigDecimal giaTien);
    Optional<ThucDon> findById(Long id);
    void deleteById(Long id);
    List<ThucDon> searchByTenMon(String keyword);
}


