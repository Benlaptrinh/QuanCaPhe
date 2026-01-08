package com.example.demo.repository;

import com.example.demo.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.demo.dto.StaffReportRowDTO;
import java.util.Optional;
import java.util.List;

public interface NhanVienRepository extends JpaRepository<NhanVien, Long> {
    Optional<NhanVien> findByTaiKhoan_MaTaiKhoan(Long maTaiKhoan);
    List<NhanVien> findByHoTenContainingIgnoreCase(String keyword);

    @Query("""
        SELECT new com.example.demo.dto.StaffReportRowDTO(
            CASE WHEN nv.enabled = true THEN 'Đang làm' ELSE 'Nghỉ việc' END,
            COUNT(nv)
        )
        FROM NhanVien nv
        GROUP BY nv.enabled
    """)
    List<StaffReportRowDTO> thongKeNhanVien();
}


