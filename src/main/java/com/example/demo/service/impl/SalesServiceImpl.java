package com.example.demo.service.impl;

import com.example.demo.entity.Ban;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.ThucDon;
import com.example.demo.repository.BanRepository;
import com.example.demo.repository.HoaDonRepository;
import com.example.demo.repository.ThucDonRepository;
import com.example.demo.repository.ChiTietHoaDonRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.demo.service.SalesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;
import com.example.demo.entity.ChiTietDatBan;
import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.NhanVien;
import com.example.demo.entity.TaiKhoan;
import com.example.demo.enums.TinhTrangBan;
import com.example.demo.enums.TrangThaiHoaDon;
import com.example.demo.repository.ChiTietDatBanRepository;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.repository.TaiKhoanRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class SalesServiceImpl implements SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    private final BanRepository banRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ThucDonRepository thucDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final ChiTietDatBanRepository chiTietDatBanRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final NhanVienRepository nhanVienRepository;

    public SalesServiceImpl(BanRepository banRepository,
                            HoaDonRepository hoaDonRepository,
                            ThucDonRepository thucDonRepository,
                            ChiTietHoaDonRepository chiTietHoaDonRepository,
                            ChiTietDatBanRepository chiTietDatBanRepository,
                            TaiKhoanRepository taiKhoanRepository,
                            NhanVienRepository nhanVienRepository) {
        this.banRepository = banRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.thucDonRepository = thucDonRepository;
        this.chiTietHoaDonRepository = chiTietHoaDonRepository;
        this.chiTietDatBanRepository = chiTietDatBanRepository;
        this.taiKhoanRepository = taiKhoanRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public List<Ban> findAllTables() {
        return banRepository.findAll();
    }

    @Override
    public Optional<HoaDon> findUnpaidInvoiceByTable(Long tableId) {
        return hoaDonRepository.findChuaThanhToanByBan(tableId);
    }

    @Override
    public List<ThucDon> findMenuItems() {
        return thucDonRepository.findAll();
    }

    @Override
    @Transactional
    public void addItemToInvoice(Long tableId, Long itemId, Integer quantity) {
        HoaDon hd = hoaDonRepository.findChuaThanhToanByBan(tableId).orElseGet(() -> {
            HoaDon n = new HoaDon();
            banRepository.findById(tableId).ifPresent(b -> {
                n.setBan(b);
                
                b.setTinhTrang(TinhTrangBan.DANG_SU_DUNG);
                banRepository.save(b);
            });
            n.setNgayGioTao(LocalDateTime.now());
            n.setTrangThai(TrangThaiHoaDon.MOI_TAO);
            return hoaDonRepository.save(n);
        });

        ThucDon item = thucDonRepository.findById(itemId).orElseThrow();
        
        ChiTietHoaDon existing = hd.getChiTietHoaDons() == null ? null :
                hd.getChiTietHoaDons().stream()
                        .filter(ct -> ct.getThucDon().getMaThucDon().equals(item.getMaThucDon()))
                        .findFirst().orElse(null);
        if (existing != null) {
            int newQty = (existing.getSoLuong() == null ? 0 : existing.getSoLuong()) + (quantity == null ? 1 : quantity);
            existing.setSoLuong(newQty);
            existing.setGiaTaiThoiDiemBan(item.getGiaHienTai());
            existing.setThanhTien(item.getGiaHienTai().multiply(new BigDecimal(newQty)));
            chiTietHoaDonRepository.save(existing);
        } else {
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setHoaDon(hd);
            ct.setThucDon(item);
            int qty = (quantity == null ? 1 : quantity);
            ct.setSoLuong(qty);
            ct.setGiaTaiThoiDiemBan(item.getGiaHienTai());
            ct.setThanhTien(item.getGiaHienTai().multiply(new BigDecimal(qty)));
            chiTietHoaDonRepository.save(ct);
            
            if (hd.getChiTietHoaDons() == null) hd.setChiTietHoaDons(new ArrayList<>());
            hd.getChiTietHoaDons().add(ct);
        }
        
        BigDecimal total = hd.getChiTietHoaDons() == null ? BigDecimal.ZERO :
                hd.getChiTietHoaDons().stream()
                        .map(ChiTietHoaDon::getThanhTien)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        hd.setTongTien(total);
        hoaDonRepository.save(hd);
    }

    @Override
    @Transactional
    public void payInvoice(Long tableId, BigDecimal tienKhach, boolean releaseTable) {
        hoaDonRepository.findChuaThanhToanByBan(tableId).ifPresent(hd -> {
            BigDecimal total = hd.getChiTietHoaDons() == null ? BigDecimal.ZERO :
                    hd.getChiTietHoaDons().stream()
                            .map(ChiTietHoaDon::getThanhTien)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
            hd.setTongTien(total);
            
            if (tienKhach.compareTo(total) < 0) {
                throw new IllegalArgumentException("Tiền khách đưa nhỏ hơn tổng");
            }
            
            hd.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
            hd.setNgayThanhToan(LocalDateTime.now());
            hoaDonRepository.save(hd);

            if (releaseTable && hd.getBan() != null) {
                hd.getBan().setTinhTrang(TinhTrangBan.TRONG);
                banRepository.save(hd.getBan());
            }
        });
    }

    @Override
    @Transactional
    public void reserveTable(Long banId, String tenKhach, String sdt, LocalDateTime ngayGioDat) {
        Ban ban = banRepository.findById(banId).orElseThrow();
        if (ban.getTinhTrang() == TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn đang phục vụ, không thể đặt");
        }
        if (ban.getTinhTrang() == TinhTrangBan.DA_DAT) {
            throw new IllegalStateException("Bàn đã được đặt trước");
        }
        ChiTietDatBan d = new ChiTietDatBan();
        d.setBan(ban);
        d.setTenKhach(tenKhach);
        d.setSdt(sdt);
        d.setNgayGioDat(ngayGioDat);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Không xác định người dùng đang đăng nhập");
        }
        String username = auth.getName();
        logger.info("reserveTable invoked by username={}", username);
        Optional<TaiKhoan> tkOpt = taiKhoanRepository.findByTenDangNhap(username);
        if (tkOpt.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy tài khoản cho user " + username);
        }
        TaiKhoan tk = tkOpt.get();
        logger.info("Found TaiKhoan ma={} for username={}", tk.getMaTaiKhoan(), username);
        Optional<NhanVien> nvOpt = nhanVienRepository.findByTaiKhoan_MaTaiKhoan(tk.getMaTaiKhoan());
        NhanVien nv;
        if (nvOpt.isEmpty()) {
            logger.warn("No NhanVien linked to TaiKhoan ma={}, will create placeholder NhanVien", tk.getMaTaiKhoan());
            nv = new NhanVien();
            nv.setHoTen(username);
            nv.setSoDienThoai(null);
            nv.setDiaChi(null);
            nv.setTaiKhoan(tk);
            nv = nhanVienRepository.save(nv);
            logger.info("Created placeholder NhanVien ma={} for TaiKhoan ma={}", nv.getMaNhanVien(), tk.getMaTaiKhoan());
        } else {
            nv = nvOpt.get();
        }
        d.setNhanVien(nv);
        logger.info("Reserve will be saved with NhanVien ma={}", nv.getMaNhanVien());
        chiTietDatBanRepository.save(d);
        ban.setTinhTrang(TinhTrangBan.DA_DAT);
        banRepository.save(ban);
    }

    @Override
    @Transactional
    public void saveSelectedMenu(Long banId, Map<String,String> params) {
        
        HoaDon hd = hoaDonRepository.findChuaThanhToanByBan(banId).orElseGet(() -> {
            HoaDon n = new HoaDon();
            banRepository.findById(banId).ifPresent(n::setBan);
            n.setNgayGioTao(LocalDateTime.now());
            n.setTrangThai(TrangThaiHoaDon.MOI_TAO);
            return hoaDonRepository.save(n);
        });

        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            taiKhoanRepository.findByTenDangNhap(username).ifPresent(tk -> {
                nhanVienRepository.findByTaiKhoan_MaTaiKhoan(tk.getMaTaiKhoan()).ifPresent(nv -> {
                    hd.setNhanVien(nv);
                });
            });
        }

        BigDecimal total = BigDecimal.ZERO;
        List<ThucDon> menu = thucDonRepository.findAll();

        if (hd.getChiTietHoaDons() == null) {
            hd.setChiTietHoaDons(new ArrayList<>());
        }

        
        Map<Long, ChiTietHoaDon> existingMap = new HashMap<>();
        for (ChiTietHoaDon ct : new ArrayList<>(hd.getChiTietHoaDons())) {
            if (ct.getThucDon() != null && ct.getThucDon().getMaThucDon() != null) {
                existingMap.put(ct.getThucDon().getMaThucDon(), ct);
            }
        }

        for (ThucDon mon : menu) {
            String key = "qty_" + (mon.getMaThucDon() == null ? "" : mon.getMaThucDon());
            int qty = 0;
            if (params.containsKey(key)) {
                try { qty = Integer.parseInt(params.get(key)); } catch (Exception ignored) {}
            }
            ChiTietHoaDon existing = existingMap.get(mon.getMaThucDon());

            if (qty > 0) {
                if (existing != null) {
                    existing.setSoLuong(qty);
                    existing.setGiaTaiThoiDiemBan(mon.getGiaHienTai());
                    existing.setThanhTien(mon.getGiaHienTai().multiply(new BigDecimal(qty)));
                } else {
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setHoaDon(hd);
                    ct.setThucDon(mon);
                    ct.setSoLuong(qty);
                    ct.setGiaTaiThoiDiemBan(mon.getGiaHienTai());
                    ct.setThanhTien(mon.getGiaHienTai().multiply(new BigDecimal(qty)));
                    hd.getChiTietHoaDons().add(ct);
                }
            } else {
                if (existing != null) {
                    
                    hd.getChiTietHoaDons().remove(existing);
                }
            }
        }

        
        for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
            if (ct.getThanhTien() != null) {
                total = total.add(ct.getThanhTien());
            }
        }

        hd.setTongTien(total);
        hoaDonRepository.save(hd);
        
        if (total.compareTo(BigDecimal.ZERO) > 0 && hd.getBan() != null) {
            Ban b = hd.getBan();
            b.setTinhTrang(TinhTrangBan.DANG_SU_DUNG);
            banRepository.save(b);
        }
    }

    @Override
    @Transactional
    public void cancelInvoice(Long banId) {
        Optional<HoaDon> hdOpt = hoaDonRepository.findChuaThanhToanByBan(banId);
        if (hdOpt.isEmpty()) {
            throw new IllegalStateException("Không có hóa đơn CHƯA THANH TOÁN cho bàn");
        }
        HoaDon hd = hdOpt.get();
        if (hd.getTrangThai() != TrangThaiHoaDon.MOI_TAO) {
            throw new IllegalStateException("Chỉ hủy hóa đơn ở trạng thái MOI_TAO");
        }

        Long hoaDonId = hd.getMaHoaDon();
        
        chiTietHoaDonRepository.deleteByHoaDonId(hoaDonId);
        
        hoaDonRepository.delete(hd);
        
        Ban b = banRepository.findById(banId).orElse(null);
        if (b != null) {
            b.setTinhTrang(TinhTrangBan.TRONG);
            banRepository.save(b);
        }
    }

    @Override
    public Optional<HoaDon> findInvoiceById(Long id) {
        return hoaDonRepository.findById(id);
    }

    @Override
    @Transactional
    public void moveTable(Long fromBanId, Long toBanId) {
        if (fromBanId.equals(toBanId)) {
            throw new IllegalArgumentException("Không thể chuyển bàn với chính nó");
        }

        Ban fromBan = banRepository.findById(fromBanId).orElseThrow(() -> new IllegalArgumentException("Bàn nguồn không tồn tại"));
        Ban toBan = banRepository.findById(toBanId).orElseThrow(() -> new IllegalArgumentException("Bàn đích không tồn tại"));

        if (fromBan.getTinhTrang() != TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn nguồn không đang sử dụng");
        }
        if (toBan.getTinhTrang() != TinhTrangBan.TRONG) {
            throw new IllegalStateException("Bàn đích không trống");
        }

        Optional<HoaDon> hdOpt = hoaDonRepository.findChuaThanhToanByBan(fromBanId);
        if (hdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn nguồn không có hóa đơn MOI_TAO");
        }
        HoaDon hd = hdOpt.get();

        
        hd.setBan(toBan);
        hoaDonRepository.save(hd);

        
        fromBan.setTinhTrang(TinhTrangBan.TRONG);
        toBan.setTinhTrang(TinhTrangBan.DANG_SU_DUNG);
        banRepository.save(fromBan);
        banRepository.save(toBan);
    }

    @Override
    public List<Ban> findEmptyTables() {
        return banRepository.findAll().stream()
                .filter(b -> b.getTinhTrang() == TinhTrangBan.TRONG)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ban> findMergeCandidates(Long excludeBanId) {
        return banRepository.findAll().stream()
                .filter(b -> b.getTinhTrang() == TinhTrangBan.DANG_SU_DUNG)
                .filter(b -> !b.getMaBan().equals(excludeBanId))
                .filter(b -> hoaDonRepository.findChuaThanhToanByBan(b.getMaBan()).isPresent())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void mergeTables(Long targetBanId, Long sourceBanId) {
        if (targetBanId.equals(sourceBanId)) {
            throw new IllegalArgumentException("Không thể gộp bàn vào chính nó");
        }

        Ban targetBan = banRepository.findById(targetBanId).orElseThrow(() -> new IllegalArgumentException("Bàn đích không tồn tại"));
        Ban sourceBan = banRepository.findById(sourceBanId).orElseThrow(() -> new IllegalArgumentException("Bàn nguồn không tồn tại"));

        if (targetBan.getTinhTrang() != TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn đích không đang sử dụng");
        }
        if (sourceBan.getTinhTrang() != TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn nguồn không đang sử dụng");
        }

        Optional<HoaDon> targetHdOpt = hoaDonRepository.findChuaThanhToanByBan(targetBanId);
        Optional<HoaDon> sourceHdOpt = hoaDonRepository.findChuaThanhToanByBan(sourceBanId);
        if (targetHdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn đích không có hóa đơn MOI_TAO");
        }
        if (sourceHdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn nguồn không có hóa đơn MOI_TAO");
        }

        HoaDon targetHd = targetHdOpt.get();
        HoaDon sourceHd = sourceHdOpt.get();

        
        if (sourceHd.getChiTietHoaDons() != null) {
            for (ChiTietHoaDon srcCt : sourceHd.getChiTietHoaDons()) {
                Long thucDonId = srcCt.getThucDon().getMaThucDon();
                ChiTietHoaDon existing = targetHd.getChiTietHoaDons() == null ? null :
                        targetHd.getChiTietHoaDons().stream()
                                .filter(ct -> ct.getThucDon().getMaThucDon().equals(thucDonId))
                                .findFirst().orElse(null);
                if (existing != null) {
                    int newQty = (existing.getSoLuong() == null ? 0 : existing.getSoLuong()) + (srcCt.getSoLuong() == null ? 0 : srcCt.getSoLuong());
                    existing.setSoLuong(newQty);
                    existing.setThanhTien(existing.getGiaTaiThoiDiemBan().multiply(new BigDecimal(newQty)));
                    chiTietHoaDonRepository.save(existing);
                } else {
                    ChiTietHoaDon newCt = new ChiTietHoaDon();
                    newCt.setHoaDon(targetHd);
                    newCt.setThucDon(srcCt.getThucDon());
                    newCt.setSoLuong(srcCt.getSoLuong());
                    newCt.setGiaTaiThoiDiemBan(srcCt.getGiaTaiThoiDiemBan());
                    newCt.setThanhTien(srcCt.getThanhTien());
                    chiTietHoaDonRepository.save(newCt);
                    if (targetHd.getChiTietHoaDons() == null) targetHd.setChiTietHoaDons(new ArrayList<>());
                    targetHd.getChiTietHoaDons().add(newCt);
                }
            }
        }

        
        BigDecimal total = targetHd.getChiTietHoaDons() == null ? BigDecimal.ZERO :
                targetHd.getChiTietHoaDons().stream()
                        .map(ChiTietHoaDon::getThanhTien)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        targetHd.setTongTien(total);
        targetHd.setTrangThai(TrangThaiHoaDon.MOI_TAO);
        hoaDonRepository.save(targetHd);

        
        sourceHd.setTrangThai(TrangThaiHoaDon.DA_GOP);
        hoaDonRepository.save(sourceHd);

        
        sourceBan.setTinhTrang(TinhTrangBan.TRONG);
        banRepository.save(sourceBan);
        targetBan.setTinhTrang(TinhTrangBan.DANG_SU_DUNG);
        banRepository.save(targetBan);
    }

    @Override
    @Transactional
    public void splitTable(Long fromBanId, Long toBanId, Map<Long, Integer> itemQuantities) {
        if (fromBanId.equals(toBanId)) {
            throw new IllegalArgumentException("Không thể tách bàn với chính nó");
        }

        Ban fromBan = banRepository.findById(fromBanId).orElseThrow(() -> new IllegalArgumentException("Bàn nguồn không tồn tại"));
        Ban toBan = banRepository.findById(toBanId).orElseThrow(() -> new IllegalArgumentException("Bàn đích không tồn tại"));

        if (fromBan.getTinhTrang() != TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn nguồn không đang sử dụng");
        }
        if (toBan.getTinhTrang() != TinhTrangBan.TRONG) {
            throw new IllegalStateException("Bàn đích phải trống");
        }

        Optional<HoaDon> fromHdOpt = hoaDonRepository.findChuaThanhToanByBan(fromBanId);
        if (fromHdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn nguồn không có hóa đơn");
        }
        HoaDon fromHd = fromHdOpt.get();

        if (itemQuantities == null || itemQuantities.isEmpty()) {
            throw new IllegalArgumentException("Không có món để tách");
        }

        
        HoaDon toHd = new HoaDon();
        toHd.setBan(toBan);
        toHd.setNgayGioTao(LocalDateTime.now());
        toHd.setTrangThai(TrangThaiHoaDon.MOI_TAO);
        toHd.setChiTietHoaDons(new ArrayList<>());
        toHd = hoaDonRepository.save(toHd);

        BigDecimal fromTotal = BigDecimal.ZERO;
        BigDecimal toTotal = BigDecimal.ZERO;

        if (fromHd.getChiTietHoaDons() != null) {
            List<ChiTietHoaDon> itemsToRemove = new ArrayList<>();

            for (ChiTietHoaDon ct : fromHd.getChiTietHoaDons()) {
                Long itemId = ct.getThucDon().getMaThucDon();
                Integer splitQty = itemQuantities.getOrDefault(itemId, 0);

                if (splitQty > 0 && splitQty < ct.getSoLuong()) {
                    
                    int remainingQty = ct.getSoLuong() - splitQty;

                    
                    ct.setSoLuong(remainingQty);
                    ct.setThanhTien(ct.getGiaTaiThoiDiemBan().multiply(new BigDecimal(remainingQty)));
                    chiTietHoaDonRepository.save(ct);
                    fromTotal = fromTotal.add(ct.getThanhTien());

                    
                    ChiTietHoaDon newCt = new ChiTietHoaDon();
                    newCt.setHoaDon(toHd);
                    newCt.setThucDon(ct.getThucDon());
                    newCt.setSoLuong(splitQty);
                    newCt.setGiaTaiThoiDiemBan(ct.getGiaTaiThoiDiemBan());
                    newCt.setThanhTien(ct.getGiaTaiThoiDiemBan().multiply(new BigDecimal(splitQty)));
                    toHd.getChiTietHoaDons().add(newCt);
                    chiTietHoaDonRepository.save(newCt);
                    toTotal = toTotal.add(newCt.getThanhTien());

                } else if (splitQty >= ct.getSoLuong()) {
                    
                    ChiTietHoaDon newCt = new ChiTietHoaDon();
                    newCt.setHoaDon(toHd);
                    newCt.setThucDon(ct.getThucDon());
                    newCt.setSoLuong(ct.getSoLuong());
                    newCt.setGiaTaiThoiDiemBan(ct.getGiaTaiThoiDiemBan());
                    newCt.setThanhTien(ct.getThanhTien());
                    chiTietHoaDonRepository.save(newCt);
                    toHd.getChiTietHoaDons().add(newCt);
                    itemsToRemove.add(ct);
                    toTotal = toTotal.add(newCt.getThanhTien());
                } else {
                    
                    fromTotal = fromTotal.add(ct.getThanhTien());
                }
            }

            
            if (!itemsToRemove.isEmpty()) {
                fromHd.getChiTietHoaDons().removeAll(itemsToRemove);
                
                chiTietHoaDonRepository.deleteAll(itemsToRemove);
            }
        }

        
        fromHd.setTongTien(fromTotal);
        hoaDonRepository.save(fromHd);

        toHd.setTongTien(toTotal);
        hoaDonRepository.save(toHd);

        
        toBan.setTinhTrang(TinhTrangBan.DANG_SU_DUNG);
        banRepository.save(toBan);
    }

    @Override
    @Transactional
    public void cancelReservation(Long banId) {
        Ban ban = banRepository.findById(banId).orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại"));

        if (ban.getTinhTrang() != TinhTrangBan.DA_DAT) {
            throw new IllegalStateException("Chỉ hủy được bàn đã đặt");
        }

        boolean hasHoaDon = hoaDonRepository.existsByBanAndTrangThai(ban, TrangThaiHoaDon.MOI_TAO);
        if (hasHoaDon) {
            throw new IllegalStateException("Bàn đã có hóa đơn, không thể hủy");
        }

        
        chiTietDatBanRepository.deleteByBan(ban);

        ban.setTinhTrang(TinhTrangBan.TRONG);
        banRepository.save(ban);
    }
}



