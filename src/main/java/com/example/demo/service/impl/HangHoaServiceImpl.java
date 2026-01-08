package com.example.demo.service.impl;

import com.example.demo.dto.HangHoaKhoDTO;
import com.example.demo.entity.HangHoa;
import com.example.demo.repository.DonNhapRepository;
import com.example.demo.repository.DonXuatRepository;
import com.example.demo.repository.HangHoaRepository;
import com.example.demo.service.HangHoaService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.dto.EditHangHoaForm;
import com.example.demo.dto.HangHoaNhapForm;
import com.example.demo.entity.DonNhap;
import com.example.demo.entity.DonViTinh;
import com.example.demo.entity.DonXuat;
import com.example.demo.entity.NhanVien;
import com.example.demo.repository.DonViTinhRepository;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HangHoaServiceImpl implements HangHoaService {

    private final HangHoaRepository hangHoaRepo;
    private final DonNhapRepository donNhapRepo;
    private final DonXuatRepository donXuatRepo;
    private final DonViTinhRepository donViTinhRepo;

    public HangHoaServiceImpl(HangHoaRepository hangHoaRepo,
                              DonNhapRepository donNhapRepo,
                              DonXuatRepository donXuatRepo,
                              DonViTinhRepository donViTinhRepo) {
        this.hangHoaRepo = hangHoaRepo;
        this.donNhapRepo = donNhapRepo;
        this.donXuatRepo = donXuatRepo;
        this.donViTinhRepo = donViTinhRepo;
    }

    @Override
    public List<HangHoaKhoDTO> getDanhSachKho() {
        List<HangHoa> list = hangHoaRepo.findAll();
        List<HangHoaKhoDTO> result = new ArrayList<>();

        for (HangHoa hh : list) {
            HangHoaKhoDTO dto = new HangHoaKhoDTO();
            dto.setMaHangHoa(hh.getMaHangHoa());
            dto.setTenHangHoa(hh.getTenHangHoa());
            dto.setSoLuong(hh.getSoLuong());
            dto.setDonGia(hh.getDonGia());

            dto.setDonVi(hh.getDonViTinh() != null ? hh.getDonViTinh().getTenDonVi() : "N/A");

            dto.setNgayNhapGanNhat(donNhapRepo.findNgayNhapGanNhat(hh.getMaHangHoa()));
            dto.setNgayXuatGanNhat(donXuatRepo.findNgayXuatGanNhat(hh.getMaHangHoa()));

            result.add(dto);
        }

        return result;
    }
    @Override
    public List<HangHoaKhoDTO> searchHangHoa(String keyword) {
        List<HangHoa> list;
        if (keyword == null || keyword.trim().isEmpty()) {
            list = hangHoaRepo.findAll();
        } else {
            list = hangHoaRepo.findByTenHangHoaContainingIgnoreCase(keyword.trim());
        }
        List<HangHoaKhoDTO> result = new ArrayList<>();
        for (HangHoa hh : list) {
            HangHoaKhoDTO dto = new HangHoaKhoDTO();
            dto.setMaHangHoa(hh.getMaHangHoa());
            dto.setTenHangHoa(hh.getTenHangHoa());
            dto.setSoLuong(hh.getSoLuong());
            dto.setDonGia(hh.getDonGia());
            dto.setDonVi(hh.getDonViTinh() != null ? hh.getDonViTinh().getTenDonVi() : "N/A");
            dto.setNgayNhapGanNhat(donNhapRepo.findNgayNhapGanNhat(hh.getMaHangHoa()));
            dto.setNgayXuatGanNhat(donXuatRepo.findNgayXuatGanNhat(hh.getMaHangHoa()));
            result.add(dto);
        }
        return result;
    }
    @Override
    @Transactional
    public void nhapHang(HangHoaNhapForm form, NhanVien nhanVien) {
        if (form.getSoLuong() == null || form.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải > 0");
        }

        HangHoa hangHoa = hangHoaRepo.findByTenHangHoa(form.getTenHangHoa()).orElse(null);

        DonViTinh donVi = null;
        if (form.getDonViTinhId() != null) {
            donVi = donViTinhRepo.findById(form.getDonViTinhId()).orElse(null);
        }

        if (hangHoa == null) {
            hangHoa = new HangHoa();
            hangHoa.setTenHangHoa(form.getTenHangHoa());
            hangHoa.setSoLuong(form.getSoLuong());
            hangHoa.setDonGia(form.getDonGia());
            hangHoa.setDonViTinh(donVi);
        } else {
            hangHoa.setSoLuong(hangHoa.getSoLuong() + form.getSoLuong());
            hangHoa.setDonGia(form.getDonGia());
        }

        hangHoaRepo.save(hangHoa);

        DonNhap dn = new DonNhap();
        dn.setHangHoa(hangHoa);
        dn.setNhanVien(nhanVien);
        dn.setSoLuong(form.getSoLuong());
        dn.setNgayNhap(form.getNgayNhap());
        
        donNhapRepo.save(dn);
    }

    @Override
    @Transactional
    public void xuatHang(Long hangHoaId, Integer soLuong, LocalDateTime ngayXuat, NhanVien nhanVien) {
        if (soLuong == null || soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải > 0");
        }
        HangHoa hh = hangHoaRepo.findById(hangHoaId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng hóa"));
        if (hh.getSoLuong() == null || hh.getSoLuong() < soLuong) {
            throw new RuntimeException("Số lượng tồn kho không đủ");
        }

        
        hh.setSoLuong(hh.getSoLuong() - soLuong);
        hangHoaRepo.save(hh);

        
        DonXuat dx = new DonXuat();
        dx.setHangHoa(hh);
        dx.setSoLuong(soLuong);
        dx.setNgayXuat(ngayXuat);
        dx.setNhanVien(nhanVien);
        donXuatRepo.save(dx);
    }

    @Override
    @Transactional
    public void updateHangHoa(EditHangHoaForm form) {
        HangHoa hh = hangHoaRepo.findById(form.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng hóa"));

        DonViTinh dvt = null;
        if (form.getDonViTinhId() != null) {
            dvt = donViTinhRepo.findById(form.getDonViTinhId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn vị"));
        }

        hh.setTenHangHoa(form.getTenHangHoa());
        hh.setSoLuong(form.getSoLuong());
        hh.setDonGia(form.getDonGia());
        hh.setDonViTinh(dvt);

        hangHoaRepo.save(hh);
    }

    @Override
    @Transactional
    public void deleteHangHoa(Long id) {
        HangHoa hh = hangHoaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng hóa"));

        boolean hasNhap = donNhapRepo.existsByHangHoa(hh);
        boolean hasXuat = donXuatRepo.existsByHangHoa(hh);

        if (hasNhap || hasXuat) {
            throw new IllegalStateException("Không thể xóa hàng hóa đã phát sinh nhập/xuất");
        }

        hangHoaRepo.delete(hh);
    }
}


