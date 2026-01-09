package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * HangHoaRepository
 *
 * Version 1.0
 *
 * Date: 09-01-2026
 *
 * Copyright
 *
 * Modification Logs:
 * DATE        AUTHOR      DESCRIPTION
 * -----------------------------------
 * 09-01-2026  Việt    Create
 */
public interface HangHoaRepository extends JpaRepository<HangHoa, Long> {
    /**
     * Find by ten hang hoa.
     *
     * @param tenHangHoa tenHangHoa
     * @return result
     */
    Optional<HangHoa> findByTenHangHoa(String tenHangHoa);
    /**
     * Find by ten hang hoa containing ignore case.
     *
     * @param keyword keyword
     * @return result
     */
    List<HangHoa> findByTenHangHoaContainingIgnoreCase(String keyword);
}


