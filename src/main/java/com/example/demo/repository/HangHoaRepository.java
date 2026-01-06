package com.example.demo.repository;

import com.example.demo.entity.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface HangHoaRepository extends JpaRepository<HangHoa, Long> {
    Optional<HangHoa> findByTenHangHoa(String tenHangHoa);
    List<HangHoa> findByTenHangHoaContainingIgnoreCase(String keyword);
}


