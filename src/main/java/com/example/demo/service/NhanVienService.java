package com.example.demo.service;

import com.example.demo.entity.NhanVien;
import java.util.List;
import java.util.Optional;

public interface NhanVienService {
    NhanVien save(NhanVien nhanVien);
    List<NhanVien> findAll();
    Optional<NhanVien> findById(Long id);
    Optional<NhanVien> findByTaiKhoanId(Long maTaiKhoan);
    void deleteByTaiKhoanId(Long maTaiKhoan);
    void deleteById(Long id);
    java.util.List<NhanVien> findByHoTenContaining(String keyword);
}

