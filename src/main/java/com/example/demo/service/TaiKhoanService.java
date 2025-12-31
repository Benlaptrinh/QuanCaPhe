package com.example.demo.service;

import com.example.demo.entity.TaiKhoan;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanService {
    TaiKhoan save(TaiKhoan taiKhoan);
    List<TaiKhoan> findAll();
    Optional<TaiKhoan> findById(Long id);
    Optional<TaiKhoan> findByUsername(String username);
    void disable(Long id);
}


