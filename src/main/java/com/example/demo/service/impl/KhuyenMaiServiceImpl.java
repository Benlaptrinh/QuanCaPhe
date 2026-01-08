package com.example.demo.service.impl;

import com.example.demo.entity.KhuyenMai;
import com.example.demo.repository.KhuyenMaiRepository;
import com.example.demo.service.KhuyenMaiService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhuyenMaiServiceImpl implements KhuyenMaiService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    public KhuyenMaiServiceImpl(KhuyenMaiRepository khuyenMaiRepository) {
        this.khuyenMaiRepository = khuyenMaiRepository;
    }

    @Override
    public List<KhuyenMai> getAllKhuyenMai() {
        return khuyenMaiRepository.findAll();
    }
    
    @Override
    public void createKhuyenMai(com.example.demo.dto.KhuyenMaiForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ");
        }

        if (form.getTenKhuyenMai() == null || form.getTenKhuyenMai().isBlank()) {
            throw new IllegalArgumentException("Chưa nhập tên khuyến mãi");
        }

        if (form.getGiaTriGiam() == null || form.getGiaTriGiam() <= 0 || form.getGiaTriGiam() > 100) {
            throw new IllegalArgumentException("Tỷ lệ giảm giá không hợp lệ (1-100)");
        }

        if (form.getNgayBatDau() != null && form.getNgayKetThuc() != null
                && form.getNgayBatDau().isAfter(form.getNgayKetThuc())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }

        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(form.getTenKhuyenMai());
        km.setNgayBatDau(form.getNgayBatDau());
        km.setNgayKetThuc(form.getNgayKetThuc());
        // convert integer percent to BigDecimal to match entity
        km.setGiaTriGiam(java.math.BigDecimal.valueOf(form.getGiaTriGiam()));

        khuyenMaiRepository.save(km);
    }
    
    @Override
    public com.example.demo.dto.KhuyenMaiForm getFormById(Long id) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));

        com.example.demo.dto.KhuyenMaiForm form = new com.example.demo.dto.KhuyenMaiForm();
        form.setTenKhuyenMai(km.getTenKhuyenMai());
        form.setNgayBatDau(km.getNgayBatDau());
        form.setNgayKetThuc(km.getNgayKetThuc());
        if (km.getGiaTriGiam() != null) {
            form.setGiaTriGiam(km.getGiaTriGiam().intValue());
        }
        return form;
    }

    @Override
    public void updateKhuyenMai(Long id, com.example.demo.dto.KhuyenMaiForm form) {
        KhuyenMai km = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));

        if (form == null) {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ");
        }

        if (form.getTenKhuyenMai() == null || form.getTenKhuyenMai().isBlank()) {
            throw new IllegalArgumentException("Chưa nhập tên khuyến mãi");
        }

        if (form.getGiaTriGiam() == null || form.getGiaTriGiam() <= 0 || form.getGiaTriGiam() > 100) {
            throw new IllegalArgumentException("Tỷ lệ giảm giá không hợp lệ (1-100)");
        }

        if (form.getNgayBatDau() != null && form.getNgayKetThuc() != null
                && form.getNgayBatDau().isAfter(form.getNgayKetThuc())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }

        km.setTenKhuyenMai(form.getTenKhuyenMai());
        km.setNgayBatDau(form.getNgayBatDau());
        km.setNgayKetThuc(form.getNgayKetThuc());
        km.setGiaTriGiam(java.math.BigDecimal.valueOf(form.getGiaTriGiam()));

        khuyenMaiRepository.save(km);
    }

    @Override
    public void deleteById(Long id) {
        khuyenMaiRepository.deleteById(id);
    }
}


