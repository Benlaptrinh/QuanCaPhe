package com.example.demo.repository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    Optional<NhanVien> findByTaiKhoan_MaTaiKhoan(Long maTaiKhoan);
    java.util.List<NhanVien> findByHoTenContainingIgnoreCase(String keyword);
}


