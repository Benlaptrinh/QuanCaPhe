package com.example.demo.service.impl;

import com.example.demo.entity.NhanVien;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.repository.TaiKhoanRepository;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.service.NhanVienService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    // save implemented below with automatic salary set from ChucVu

    @Override
    public List<NhanVien> findAll() {
        return nhanVienRepository.findAll();
    }

    @Override
    public Optional<NhanVien> findById(Long id) {
        return nhanVienRepository.findById(id);
    }

    @Override
    public Optional<NhanVien> findByTaiKhoanId(Long maTaiKhoan) {
        return nhanVienRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan);
    }

    @Override
    public java.util.List<NhanVien> findByHoTenContaining(String keyword) {
        return nhanVienRepository.findByHoTenContainingIgnoreCase(keyword);
    }

    @Override
    public NhanVien save(NhanVien nhanVien) {
        if (nhanVien.getChucVu() != null && nhanVien.getChucVu().getLuong() != null) {
            nhanVien.setLuong(nhanVien.getChucVu().getLuong());
        }
        return nhanVienRepository.save(nhanVien);
    }

    @Override
    @Transactional
    public void deleteByTaiKhoanId(Long maTaiKhoan) {
        nhanVienRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan).ifPresent(nv -> {
            if (nv.getTaiKhoan() != null) {
                taiKhoanRepository.delete(nv.getTaiKhoan());
            }
            nhanVienRepository.delete(nv);
        });
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        nhanVienRepository.findById(id).ifPresent(nv -> {
            if (nv.getTaiKhoan() != null) {
                taiKhoanRepository.delete(nv.getTaiKhoan());
            }
            nhanVienRepository.delete(nv);
        });
    }
}


