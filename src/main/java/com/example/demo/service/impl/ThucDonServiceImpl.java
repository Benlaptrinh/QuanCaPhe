package com.example.demo.service.impl;

import com.example.demo.entity.ThucDon;
import com.example.demo.repository.ThucDonRepository;
import com.example.demo.service.ThucDonService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class ThucDonServiceImpl implements ThucDonService {

    private final ThucDonRepository thucDonRepository;

    public ThucDonServiceImpl(ThucDonRepository thucDonRepository) {
        this.thucDonRepository = thucDonRepository;
    }

    @Override
    public List<ThucDon> findAll() {
        return thucDonRepository.findAll();
    }
    
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

    @Override
    public Optional<ThucDon> findById(Long id) {
        return thucDonRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        ThucDon thucDon = thucDonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món"));
        thucDonRepository.delete(thucDon);
    }

    @Override
    public List<ThucDon> searchByTenMon(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return thucDonRepository.findAll();
        }
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword.trim());
    }
}


