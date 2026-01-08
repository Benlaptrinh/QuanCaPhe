package com.example.demo.service.impl;

import com.example.demo.entity.ThietBi;
import com.example.demo.repository.ThietBiRepository;
import com.example.demo.service.ThietBiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ThietBiServiceImpl implements ThietBiService {

    private final ThietBiRepository thietBiRepository;

    public ThietBiServiceImpl(ThietBiRepository thietBiRepository) {
        this.thietBiRepository = thietBiRepository;
    }

    @Override
    public List<ThietBi> findAll() {
        return thietBiRepository.findAll();
    }

    @Override
    public Optional<ThietBi> findById(Long id) {
        return thietBiRepository.findById(id);
    }

    @Override
    @Transactional
    public ThietBi save(ThietBi thietBi) {
        // basic validation before save
        if (thietBi.getTenThietBi() == null || thietBi.getTenThietBi().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên thiết bị là bắt buộc");
        }
        if (thietBi.getSoLuong() == null || thietBi.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        if (thietBi.getDonGiaMua() == null || thietBi.getDonGiaMua().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Đơn giá phải >= 0");
        }
        return thietBiRepository.save(thietBi);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        thietBiRepository.deleteById(id);
    }
}


