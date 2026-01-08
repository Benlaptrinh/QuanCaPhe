package com.example.demo.service;

import com.example.demo.entity.KhuyenMai;
import java.util.List;
import com.example.demo.dto.KhuyenMaiForm;

public interface KhuyenMaiService {
    List<KhuyenMai> getAllKhuyenMai();
    void createKhuyenMai(KhuyenMaiForm form);
    KhuyenMaiForm getFormById(Long id);
    void updateKhuyenMai(Long id, KhuyenMaiForm form);
    void deleteById(Long id);
}


