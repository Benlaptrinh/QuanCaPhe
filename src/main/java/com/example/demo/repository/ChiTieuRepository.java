package com.example.demo.repository;

import com.example.demo.entity.ChiTieu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ChiTieuRepository extends JpaRepository<ChiTieu, Long> {
    List<ChiTieu> findByNgayChiBetween(LocalDate from, LocalDate to);
}


