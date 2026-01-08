package com.example.demo.repository;

import com.example.demo.entity.DonNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import com.example.demo.entity.HangHoa;

public interface DonNhapRepository extends JpaRepository<DonNhap, Long> {

    @Query("""
        select max(d.ngayNhap)
        from DonNhap d
        where d.hangHoa.maHangHoa = :id
    """)
    LocalDateTime findNgayNhapGanNhat(@Param("id") Long hangHoaId);
    boolean existsByHangHoa(HangHoa hangHoa);
}


