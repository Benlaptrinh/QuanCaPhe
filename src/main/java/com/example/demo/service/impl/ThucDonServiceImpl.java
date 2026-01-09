package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.ThucDon;
import com.example.demo.repository.ThucDonRepository;
import com.example.demo.service.ThucDonService;
import org.springframework.stereotype.Service;

/**
 * ThucDonServiceImpl
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
@Service
public class ThucDonServiceImpl implements ThucDonService {

    private final ThucDonRepository thucDonRepository;

    /**
     * Creates ThucDonServiceImpl.
     *
     * @param thucDonRepository thucDonRepository
     */
    public ThucDonServiceImpl(ThucDonRepository thucDonRepository) {
        this.thucDonRepository = thucDonRepository;
    }

    /**
     * Find all.
     *
     * @return result
     */
    @Override
    public List<ThucDon> findAll() {
        return thucDonRepository.findAll();
    }
    
    /**
     * Create.
     *
     * @param tenMon tenMon
     * @param giaTien giaTien
     */
    @Override
    public void create(String tenMon, BigDecimal giaTien) {
        if (tenMon == null || tenMon.isBlank() || giaTien == null) {
            throw new IllegalArgumentException("Chưa nhập các trường bắt buộc");
        }

        thucDonRepository.findByTenMonIgnoreCase(tenMon)
                .ifPresent(m -> {
                    throw new IllegalArgumentException("Tên món đã tồn tại");
                });

        ThucDon thucDon = new ThucDon();
        thucDon.setTenMon(tenMon);
        thucDon.setGiaHienTai(giaTien);

        thucDonRepository.save(thucDon);
    }
    
    /**
     * Update.
     *
     * @param id id
     * @param tenMon tenMon
     * @param giaTien giaTien
     */
    @Override
    public void update(Long id, String tenMon, BigDecimal giaTien) {
        if (tenMon == null || tenMon.isBlank() || giaTien == null) {
            throw new IllegalArgumentException("Chưa nhập các trường bắt buộc");
        }

        ThucDon thucDon = thucDonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món"));

        thucDonRepository.findByTenMonIgnoreCase(tenMon)
                .ifPresent(m -> {
                    if (!m.getMaThucDon().equals(id)) {
                        throw new IllegalArgumentException("Tên món đã tồn tại");
                    }
                });

        thucDon.setTenMon(tenMon);
        thucDon.setGiaHienTai(giaTien);
        thucDonRepository.save(thucDon);
    }

    /**
     * Find by id.
     *
     * @param id id
     * @return result
     */
    @Override
    public Optional<ThucDon> findById(Long id) {
        return thucDonRepository.findById(id);
    }

    /**
     * Delete by id.
     *
     * @param id id
     */
    @Override
    public void deleteById(Long id) {
        ThucDon thucDon = thucDonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món"));
        thucDonRepository.delete(thucDon);
    }

    /**
     * Search by ten mon.
     *
     * @param keyword keyword
     * @return result
     */
    @Override
    public List<ThucDon> searchByTenMon(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return thucDonRepository.findAll();
        }
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword.trim());
    }
}


