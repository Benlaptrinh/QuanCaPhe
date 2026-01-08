package com.example.demo.service;

import com.example.demo.dto.HangHoaKhoDTO;
import java.util.List;

public interface HangHoaService {
    List<HangHoaKhoDTO> getDanhSachKho();
    void nhapHang(com.example.demo.dto.HangHoaNhapForm form, com.example.demo.entity.NhanVien nhanVien);
    void xuatHang(Long hangHoaId, Integer soLuong, java.time.LocalDateTime ngayXuat, com.example.demo.entity.NhanVien nhanVien);
    void updateHangHoa(com.example.demo.dto.EditHangHoaForm form);
    void deleteHangHoa(Long id);
    java.util.List<com.example.demo.dto.HangHoaKhoDTO> searchHangHoa(String keyword);
}


