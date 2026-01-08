package com.example.demo.service;

import com.example.demo.dto.HangHoaKhoDTO;
import java.util.List;
import com.example.demo.dto.EditHangHoaForm;
import com.example.demo.dto.HangHoaNhapForm;
import com.example.demo.entity.NhanVien;
import java.time.LocalDateTime;

public interface HangHoaService {
    List<HangHoaKhoDTO> getDanhSachKho();
    void nhapHang(HangHoaNhapForm form, NhanVien nhanVien);
    void xuatHang(Long hangHoaId, Integer soLuong, LocalDateTime ngayXuat, NhanVien nhanVien);
    void updateHangHoa(EditHangHoaForm form);
    void deleteHangHoa(Long id);
    List<HangHoaKhoDTO> searchHangHoa(String keyword);
}


