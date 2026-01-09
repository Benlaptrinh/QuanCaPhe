package com.example.demo.service.impl;

import java.util.List;
import java.util.Optional;

import com.example.demo.entity.NhanVien;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.repository.TaiKhoanRepository;
import com.example.demo.service.NhanVienService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * NhanVienServiceImpl
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
public class NhanVienServiceImpl implements NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;

    /**
     * Creates NhanVienServiceImpl.
     *
     * @param nhanVienRepository nhanVienRepository
     * @param taiKhoanRepository taiKhoanRepository
     */
    public NhanVienServiceImpl(NhanVienRepository nhanVienRepository, TaiKhoanRepository taiKhoanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    

    /**
     * Find all.
     *
     * @return result
     */
    @Override
    public List<NhanVien> findAll() {
        return nhanVienRepository.findAll();
    }

    /**
     * Find by id.
     *
     * @param id id
     * @return result
     */
    @Override
    public Optional<NhanVien> findById(Long id) {
        return nhanVienRepository.findById(id);
    }

    /**
     * Find by tai khoan id.
     *
     * @param maTaiKhoan maTaiKhoan
     * @return result
     */
    @Override
    public Optional<NhanVien> findByTaiKhoanId(Long maTaiKhoan) {
        return nhanVienRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan);
    }

    /**
     * Find by ho ten containing.
     *
     * @param keyword keyword
     * @return result
     */
    @Override
    public List<NhanVien> findByHoTenContaining(String keyword) {
        return nhanVienRepository.findByHoTenContainingIgnoreCase(keyword);
    }

    /**
     * Save.
     *
     * @param nhanVien nhanVien
     * @return result
     */
    @Override
    public NhanVien save(NhanVien nhanVien) {
        if (nhanVien.getChucVu() != null && nhanVien.getChucVu().getLuong() != null) {
            nhanVien.setLuong(nhanVien.getChucVu().getLuong());
        }
        return nhanVienRepository.save(nhanVien);
    }

    /**
     * Delete by tai khoan id.
     *
     * @param maTaiKhoan maTaiKhoan
     */
    @Override
    /**
     * Delete by tai khoan id.
     *
     * @param maTaiKhoan maTaiKhoan
     */
    @Transactional
    public void deleteByTaiKhoanId(Long maTaiKhoan) {
        nhanVienRepository.findByTaiKhoan_MaTaiKhoan(maTaiKhoan).ifPresent(nv -> {
            if (nv.getTaiKhoan() != null) {
                taiKhoanRepository.delete(nv.getTaiKhoan());
            }
            nhanVienRepository.delete(nv);
        });
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
        nhanVienRepository.findById(id).ifPresent(nv -> {
            if (nv.getTaiKhoan() != null) {
                taiKhoanRepository.delete(nv.getTaiKhoan());
            }
            nhanVienRepository.delete(nv);
        });
    }
}


