package com.example.demo.repository;

import com.example.demo.entity.ThucDon;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface ThucDonRepository extends JpaRepository<ThucDon, Long> {
    Optional<ThucDon> findByTenMonIgnoreCase(String tenMon);
    List<ThucDon> findByTenMonContainingIgnoreCase(String tenMon);
}



