package com.example.demo.repository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.dto.StaffReportRowDTO;
import java.util.Optional;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    Optional<NhanVien> findByTaiKhoan_MaTaiKhoan(Long maTaiKhoan);
    java.util.List<NhanVien> findByHoTenContainingIgnoreCase(String keyword);

    @Query("""
        SELECT new com.example.demo.dto.StaffReportRowDTO(
            CASE WHEN nv.enabled = true THEN 'Đang làm' ELSE 'Nghỉ việc' END,
            COUNT(nv),
            COALESCE(SUM(nv.luong), 0)
        )
        FROM NhanVien nv
        GROUP BY nv.enabled
    """)
    java.util.List<StaffReportRowDTO> thongKeNhanVien();
}


