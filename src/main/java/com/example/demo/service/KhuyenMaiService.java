package com.example.demo.service;

import com.example.demo.entity.KhuyenMai;
import java.util.List;

public interface KhuyenMaiService {
    List<KhuyenMai> getAllKhuyenMai();
    void createKhuyenMai(com.example.demo.dto.KhuyenMaiForm form);
    com.example.demo.dto.KhuyenMaiForm getFormById(Long id);
    void updateKhuyenMai(Long id, com.example.demo.dto.KhuyenMaiForm form);
    void deleteById(Long id);
}


