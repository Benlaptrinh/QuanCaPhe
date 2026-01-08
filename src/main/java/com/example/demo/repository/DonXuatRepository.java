package com.example.demo.repository;

import com.example.demo.entity.DonXuat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface DonXuatRepository extends JpaRepository<DonXuat, Long> {

    @Query("""
        select max(d.ngayXuat)
        from DonXuat d
        where d.hangHoa.maHangHoa = :id
    """)
    LocalDateTime findNgayXuatGanNhat(@Param("id") Long hangHoaId);
    boolean existsByHangHoa(com.example.demo.entity.HangHoa hangHoa);
}



