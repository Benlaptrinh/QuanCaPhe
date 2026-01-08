package com.example.demo.repository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.report.dto.StaffReportRowDTO;
import java.util.Optional;
import java.util.List;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    Optional<NhanVien> findByTaiKhoan_MaTaiKhoan(Long maTaiKhoan);
    List<NhanVien> findByHoTenContainingIgnoreCase(String keyword);

    @Query("""
        SELECT nv.enabled, COUNT(nv)
        FROM NhanVien nv
        GROUP BY nv.enabled
    """)
    List<Object[]> thongKeNhanVienRaw();
    
    
    
}


