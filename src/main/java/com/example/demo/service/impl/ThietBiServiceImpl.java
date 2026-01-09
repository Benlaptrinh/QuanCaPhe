package com.example.demo.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.example.demo.entity.ThietBi;
import com.example.demo.repository.ThietBiRepository;
import com.example.demo.service.ThietBiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ThietBiServiceImpl
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
public class ThietBiServiceImpl implements ThietBiService {

    private final ThietBiRepository thietBiRepository;

    /**
     * Creates ThietBiServiceImpl.
     *
     * @param thietBiRepository thietBiRepository
     */
    public ThietBiServiceImpl(ThietBiRepository thietBiRepository) {
        this.thietBiRepository = thietBiRepository;
    }

    /**
     * Find all.
     *
     * @return result
     */
    @Override
    public List<ThietBi> findAll() {
        return thietBiRepository.findAll();
    }

    /**
     * Find by id.
     *
     * @param id id
     * @return result
     */
    @Override
    public Optional<ThietBi> findById(Long id) {
        return thietBiRepository.findById(id);
    }

    /**
     * Save.
     *
     * @param thietBi thietBi
     * @return result
     */
    @Override
    /**
     * Save.
     *
     * @param thietBi thietBi
     * @return result
     */
    @Transactional
    public ThietBi save(ThietBi thietBi) {
        
        if (thietBi.getTenThietBi() == null || thietBi.getTenThietBi().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thiết bị là bắt buộc");
        }
        if (thietBi.getSoLuong() == null || thietBi.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (thietBi.getDonGiaMua() == null || thietBi.getDonGiaMua().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá phải >= 0");
        }
        return thietBiRepository.save(thietBi);
    }

    /**
     * Delete by id.
     *
     * @param id id
     */
    @Override
    /**
     * Delete by id.
     *
     * @param id id
     */
    @Transactional
    public void deleteById(Long id) {
        thietBiRepository.deleteById(id);
    }
}


