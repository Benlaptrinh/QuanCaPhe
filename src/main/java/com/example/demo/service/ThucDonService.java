package com.example.demo.service;

import com.example.demo.entity.ThucDon;
import java.util.List;

public interface ThucDonService {
    List<ThucDon> findAll();
    void create(String tenMon, java.math.BigDecimal giaTien);
    void update(Long id, String tenMon, java.math.BigDecimal giaTien);
    java.util.Optional<ThucDon> findById(Long id);
    void deleteById(Long id);
    java.util.List<ThucDon> searchByTenMon(String keyword);
}


