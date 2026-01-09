package com.example.demo.repository;

import com.example.demo.entity.Ban;
import com.example.demo.entity.ChiTietDatBan;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ChiTietDatBanRepository
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
public interface ChiTietDatBanRepository extends JpaRepository<ChiTietDatBan, Long> {
    /**
     * Delete by ban.
     *
     * @param ban ban
     */
    void deleteByBan(Ban ban);
}


