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

@Service
public class SalesServiceImpl implements SalesService {

    private static final Logger logger = LoggerFactory.getLogger(SalesServiceImpl.class);

    private final BanRepository banRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ThucDonRepository thucDonRepository;
    private final ChiTietHoaDonRepository chiTietHoaDonRepository;
    private final com.example.demo.repository.ChiTietDatBanRepository chiTietDatBanRepository;
    private final com.example.demo.repository.TaiKhoanRepository taiKhoanRepository;
    private final com.example.demo.repository.NhanVienRepository nhanVienRepository;

    public SalesServiceImpl(BanRepository banRepository,
                            HoaDonRepository hoaDonRepository,
                            ThucDonRepository thucDonRepository,
                            ChiTietHoaDonRepository chiTietHoaDonRepository,
                            com.example.demo.repository.ChiTietDatBanRepository chiTietDatBanRepository,
                            com.example.demo.repository.TaiKhoanRepository taiKhoanRepository,
                            com.example.demo.repository.NhanVienRepository nhanVienRepository) {
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
                // mark table as occupied
                b.setTinhTrang(com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG);
                banRepository.save(b);
            });
            n.setNgayGioTao(LocalDateTime.now());
            n.setTrangThai(com.example.demo.enums.TrangThaiHoaDon.MOI_TAO);
            return hoaDonRepository.save(n);
        });

        ThucDon item = thucDonRepository.findById(itemId).orElseThrow();
        // find existing chi tiet
        com.example.demo.entity.ChiTietHoaDon existing = hd.getChiTietHoaDons() == null ? null :
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
            com.example.demo.entity.ChiTietHoaDon ct = new com.example.demo.entity.ChiTietHoaDon();
            ct.setHoaDon(hd);
            ct.setThucDon(item);
            int qty = (quantity == null ? 1 : quantity);
            ct.setSoLuong(qty);
            ct.setGiaTaiThoiDiemBan(item.getGiaHienTai());
            ct.setThanhTien(item.getGiaHienTai().multiply(new BigDecimal(qty)));
            chiTietHoaDonRepository.save(ct);
            // attach to hd list
            if (hd.getChiTietHoaDons() == null) hd.setChiTietHoaDons(new java.util.ArrayList<>());
            hd.getChiTietHoaDons().add(ct);
        }
        // update total
        BigDecimal total = hd.getChiTietHoaDons() == null ? BigDecimal.ZERO :
                hd.getChiTietHoaDons().stream()
                        .map(com.example.demo.entity.ChiTietHoaDon::getThanhTien)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        hd.setTongTien(total);
        hoaDonRepository.save(hd);
    }

    @Override
    @Transactional
    public void payInvoice(Long tableId, java.math.BigDecimal tienKhach, boolean releaseTable) {
        hoaDonRepository.findChuaThanhToanByBan(tableId).ifPresent(hd -> {
            java.math.BigDecimal total = hd.getChiTietHoaDons() == null ? java.math.BigDecimal.ZERO :
                    hd.getChiTietHoaDons().stream()
                            .map(com.example.demo.entity.ChiTietHoaDon::getThanhTien)
                            .filter(java.util.Objects::nonNull)
                            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            hd.setTongTien(total);
            // validate
            if (tienKhach.compareTo(total) < 0) {
                throw new IllegalArgumentException("Tiền khách đưa nhỏ hơn tổng");
            }
            // set payment info
            hd.setTrangThai(com.example.demo.enums.TrangThaiHoaDon.DA_THANH_TOAN);
            hd.setNgayThanhToan(java.time.LocalDateTime.now());
            hoaDonRepository.save(hd);

            if (releaseTable && hd.getBan() != null) {
                hd.getBan().setTinhTrang(com.example.demo.enums.TinhTrangBan.TRONG);
                banRepository.save(hd.getBan());
            }
        });
    }

    @Override
    @Transactional
    public void reserveTable(Long banId, String tenKhach, String sdt, java.time.LocalDateTime ngayGioDat) {
        Ban ban = banRepository.findById(banId).orElseThrow();
        if (ban.getTinhTrang() == com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn đang phục vụ, không thể đặt");
        }
        if (ban.getTinhTrang() == com.example.demo.enums.TinhTrangBan.DA_DAT) {
            throw new IllegalStateException("Bàn đã được đặt trước");
        }
        com.example.demo.entity.ChiTietDatBan d = new com.example.demo.entity.ChiTietDatBan();
        d.setBan(ban);
        d.setTenKhach(tenKhach);
        d.setSdt(sdt);
        d.setNgayGioDat(ngayGioDat);
        // attach current logged-in employee; require mapping exists
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Không xác định người dùng đang đăng nhập");
        }
        String username = auth.getName();
        logger.info("reserveTable invoked by username={}", username);
        java.util.Optional<com.example.demo.entity.TaiKhoan> tkOpt = taiKhoanRepository.findByTenDangNhap(username);
        if (tkOpt.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy tài khoản cho user " + username);
        }
        com.example.demo.entity.TaiKhoan tk = tkOpt.get();
        logger.info("Found TaiKhoan ma={} for username={}", tk.getMaTaiKhoan(), username);
        java.util.Optional<com.example.demo.entity.NhanVien> nvOpt = nhanVienRepository.findByTaiKhoan_MaTaiKhoan(tk.getMaTaiKhoan());
        com.example.demo.entity.NhanVien nv;
        if (nvOpt.isEmpty()) {
            logger.warn("No NhanVien linked to TaiKhoan ma={}, will create placeholder NhanVien", tk.getMaTaiKhoan());
            nv = new com.example.demo.entity.NhanVien();
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
        ban.setTinhTrang(com.example.demo.enums.TinhTrangBan.DA_DAT);
        banRepository.save(ban);
    }

    @Override
    @Transactional
    public void saveSelectedMenu(Long banId, java.util.Map<String,String> params) {
        // get or create invoice
        HoaDon hd = hoaDonRepository.findChuaThanhToanByBan(banId).orElseGet(() -> {
            HoaDon n = new HoaDon();
            banRepository.findById(banId).ifPresent(n::setBan);
            n.setNgayGioTao(java.time.LocalDateTime.now());
            n.setTrangThai(com.example.demo.enums.TrangThaiHoaDon.MOI_TAO);
            return hoaDonRepository.save(n);
        });

        // attach current logged-in employee to the invoice for traceability
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String username = auth.getName();
            taiKhoanRepository.findByTenDangNhap(username).ifPresent(tk -> {
                nhanVienRepository.findByTaiKhoan_MaTaiKhoan(tk.getMaTaiKhoan()).ifPresent(nv -> {
                    hd.setNhanVien(nv);
                });
            });
        }

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        java.util.List<ThucDon> menu = thucDonRepository.findAll();

        if (hd.getChiTietHoaDons() == null) {
            hd.setChiTietHoaDons(new java.util.ArrayList<>());
        }

        // build quick lookup of existing details by thucDon id
        java.util.Map<Long, com.example.demo.entity.ChiTietHoaDon> existingMap = new java.util.HashMap<>();
        for (com.example.demo.entity.ChiTietHoaDon ct : new java.util.ArrayList<>(hd.getChiTietHoaDons())) {
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
            com.example.demo.entity.ChiTietHoaDon existing = existingMap.get(mon.getMaThucDon());

            if (qty > 0) {
                if (existing != null) {
                    existing.setSoLuong(qty);
                    existing.setGiaTaiThoiDiemBan(mon.getGiaHienTai());
                    existing.setThanhTien(mon.getGiaHienTai().multiply(new java.math.BigDecimal(qty)));
                } else {
                    com.example.demo.entity.ChiTietHoaDon ct = new com.example.demo.entity.ChiTietHoaDon();
                    ct.setHoaDon(hd);
                    ct.setThucDon(mon);
                    ct.setSoLuong(qty);
                    ct.setGiaTaiThoiDiemBan(mon.getGiaHienTai());
                    ct.setThanhTien(mon.getGiaHienTai().multiply(new java.math.BigDecimal(qty)));
                    hd.getChiTietHoaDons().add(ct);
                }
            } else {
                if (existing != null) {
                    // remove from the invoice collection; orphanRemoval=true will delete it on save
                    hd.getChiTietHoaDons().remove(existing);
                }
            }
        }

        // recalc total from remaining details
        for (com.example.demo.entity.ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
            if (ct.getThanhTien() != null) {
                total = total.add(ct.getThanhTien());
            }
        }

        hd.setTongTien(total);
        hoaDonRepository.save(hd);
        // if at least one item, mark table as serving
        if (total.compareTo(java.math.BigDecimal.ZERO) > 0 && hd.getBan() != null) {
            Ban b = hd.getBan();
            b.setTinhTrang(com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG);
            banRepository.save(b);
        }
    }

    @Override
    @Transactional
    public void cancelInvoice(Long banId) {
        java.util.Optional<HoaDon> hdOpt = hoaDonRepository.findChuaThanhToanByBan(banId);
        if (hdOpt.isEmpty()) {
            throw new IllegalStateException("Không có hóa đơn CHƯA THANH TOÁN cho bàn");
        }
        HoaDon hd = hdOpt.get();
        if (hd.getTrangThai() != com.example.demo.enums.TrangThaiHoaDon.MOI_TAO) {
            throw new IllegalStateException("Chỉ hủy hóa đơn ở trạng thái MOI_TAO");
        }

        Long hoaDonId = hd.getMaHoaDon();
        // delete details first (recommended)
        chiTietHoaDonRepository.deleteByHoaDonId(hoaDonId);
        // then delete invoice
        hoaDonRepository.delete(hd);
        // set table to TRONG
        Ban b = banRepository.findById(banId).orElse(null);
        if (b != null) {
            b.setTinhTrang(com.example.demo.enums.TinhTrangBan.TRONG);
            banRepository.save(b);
        }
    }

    @Override
    public java.util.Optional<com.example.demo.entity.HoaDon> findInvoiceById(Long id) {
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

        if (fromBan.getTinhTrang() != com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn nguồn không đang sử dụng");
        }
        if (toBan.getTinhTrang() != com.example.demo.enums.TinhTrangBan.TRONG) {
            throw new IllegalStateException("Bàn đích không trống");
        }

        java.util.Optional<HoaDon> hdOpt = hoaDonRepository.findChuaThanhToanByBan(fromBanId);
        if (hdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn nguồn không có hóa đơn MOI_TAO");
        }
        HoaDon hd = hdOpt.get();

        // move invoice to target table
        hd.setBan(toBan);
        hoaDonRepository.save(hd);

        // update table statuses
        fromBan.setTinhTrang(com.example.demo.enums.TinhTrangBan.TRONG);
        toBan.setTinhTrang(com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG);
        banRepository.save(fromBan);
        banRepository.save(toBan);
    }

    @Override
    public java.util.List<Ban> findEmptyTables() {
        return banRepository.findAll().stream()
                .filter(b -> b.getTinhTrang() == com.example.demo.enums.TinhTrangBan.TRONG)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<Ban> findMergeCandidates(Long excludeBanId) {
        return banRepository.findAll().stream()
                .filter(b -> b.getTinhTrang() == com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG)
                .filter(b -> !b.getMaBan().equals(excludeBanId))
                .filter(b -> hoaDonRepository.findChuaThanhToanByBan(b.getMaBan()).isPresent())
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public void mergeTables(Long targetBanId, Long sourceBanId) {
        if (targetBanId.equals(sourceBanId)) {
            throw new IllegalArgumentException("Không thể gộp bàn vào chính nó");
        }

        Ban targetBan = banRepository.findById(targetBanId).orElseThrow(() -> new IllegalArgumentException("Bàn đích không tồn tại"));
        Ban sourceBan = banRepository.findById(sourceBanId).orElseThrow(() -> new IllegalArgumentException("Bàn nguồn không tồn tại"));

        if (targetBan.getTinhTrang() != com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn đích không đang sử dụng");
        }
        if (sourceBan.getTinhTrang() != com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn nguồn không đang sử dụng");
        }

        java.util.Optional<HoaDon> targetHdOpt = hoaDonRepository.findChuaThanhToanByBan(targetBanId);
        java.util.Optional<HoaDon> sourceHdOpt = hoaDonRepository.findChuaThanhToanByBan(sourceBanId);
        if (targetHdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn đích không có hóa đơn MOI_TAO");
        }
        if (sourceHdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn nguồn không có hóa đơn MOI_TAO");
        }

        HoaDon targetHd = targetHdOpt.get();
        HoaDon sourceHd = sourceHdOpt.get();

        // merge details by copying (preserve source invoice for audit)
        if (sourceHd.getChiTietHoaDons() != null) {
            for (com.example.demo.entity.ChiTietHoaDon srcCt : sourceHd.getChiTietHoaDons()) {
                Long thucDonId = srcCt.getThucDon().getMaThucDon();
                com.example.demo.entity.ChiTietHoaDon existing = targetHd.getChiTietHoaDons() == null ? null :
                        targetHd.getChiTietHoaDons().stream()
                                .filter(ct -> ct.getThucDon().getMaThucDon().equals(thucDonId))
                                .findFirst().orElse(null);
                if (existing != null) {
                    int newQty = (existing.getSoLuong() == null ? 0 : existing.getSoLuong()) + (srcCt.getSoLuong() == null ? 0 : srcCt.getSoLuong());
                    existing.setSoLuong(newQty);
                    existing.setThanhTien(existing.getGiaTaiThoiDiemBan().multiply(new BigDecimal(newQty)));
                    chiTietHoaDonRepository.save(existing);
                } else {
                    com.example.demo.entity.ChiTietHoaDon newCt = new com.example.demo.entity.ChiTietHoaDon();
                    newCt.setHoaDon(targetHd);
                    newCt.setThucDon(srcCt.getThucDon());
                    newCt.setSoLuong(srcCt.getSoLuong());
                    newCt.setGiaTaiThoiDiemBan(srcCt.getGiaTaiThoiDiemBan());
                    newCt.setThanhTien(srcCt.getThanhTien());
                    chiTietHoaDonRepository.save(newCt);
                    if (targetHd.getChiTietHoaDons() == null) targetHd.setChiTietHoaDons(new java.util.ArrayList<>());
                    targetHd.getChiTietHoaDons().add(newCt);
                }
            }
        }

        // recalc total for target
        BigDecimal total = targetHd.getChiTietHoaDons() == null ? BigDecimal.ZERO :
                targetHd.getChiTietHoaDons().stream()
                        .map(com.example.demo.entity.ChiTietHoaDon::getThanhTien)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        targetHd.setTongTien(total);
        targetHd.setTrangThai(com.example.demo.enums.TrangThaiHoaDon.MOI_TAO);
        hoaDonRepository.save(targetHd);

        // mark source invoice as DA_GOP (do not delete)
        sourceHd.setTrangThai(com.example.demo.enums.TrangThaiHoaDon.DA_GOP);
        hoaDonRepository.save(sourceHd);

        // update table statuses
        sourceBan.setTinhTrang(com.example.demo.enums.TinhTrangBan.TRONG);
        banRepository.save(sourceBan);
        targetBan.setTinhTrang(com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG);
        banRepository.save(targetBan);
    }

    @Override
    @Transactional
    public void splitTable(Long fromBanId, Long toBanId, java.util.Map<Long, Integer> itemQuantities) {
        if (fromBanId.equals(toBanId)) {
            throw new IllegalArgumentException("Không thể tách bàn với chính nó");
        }

        Ban fromBan = banRepository.findById(fromBanId).orElseThrow(() -> new IllegalArgumentException("Bàn nguồn không tồn tại"));
        Ban toBan = banRepository.findById(toBanId).orElseThrow(() -> new IllegalArgumentException("Bàn đích không tồn tại"));

        if (fromBan.getTinhTrang() != com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG) {
            throw new IllegalStateException("Bàn nguồn không đang sử dụng");
        }
        if (toBan.getTinhTrang() != com.example.demo.enums.TinhTrangBan.TRONG) {
            throw new IllegalStateException("Bàn đích phải trống");
        }

        java.util.Optional<HoaDon> fromHdOpt = hoaDonRepository.findChuaThanhToanByBan(fromBanId);
        if (fromHdOpt.isEmpty()) {
            throw new IllegalStateException("Bàn nguồn không có hóa đơn");
        }
        HoaDon fromHd = fromHdOpt.get();

        if (itemQuantities == null || itemQuantities.isEmpty()) {
            throw new IllegalArgumentException("Không có món để tách");
        }

        // create new invoice for target table
        HoaDon toHd = new HoaDon();
        toHd.setBan(toBan);
        toHd.setNgayGioTao(LocalDateTime.now());
        toHd.setTrangThai(com.example.demo.enums.TrangThaiHoaDon.MOI_TAO);
        toHd.setChiTietHoaDons(new java.util.ArrayList<>());
        toHd = hoaDonRepository.save(toHd);

        BigDecimal fromTotal = BigDecimal.ZERO;
        BigDecimal toTotal = BigDecimal.ZERO;

        if (fromHd.getChiTietHoaDons() != null) {
            java.util.List<com.example.demo.entity.ChiTietHoaDon> itemsToRemove = new java.util.ArrayList<>();

            for (com.example.demo.entity.ChiTietHoaDon ct : fromHd.getChiTietHoaDons()) {
                Long itemId = ct.getThucDon().getMaThucDon();
                Integer splitQty = itemQuantities.getOrDefault(itemId, 0);

                if (splitQty > 0 && splitQty < ct.getSoLuong()) {
                    // Split this item
                    int remainingQty = ct.getSoLuong() - splitQty;

                    // Update source item
                    ct.setSoLuong(remainingQty);
                    ct.setThanhTien(ct.getGiaTaiThoiDiemBan().multiply(new BigDecimal(remainingQty)));
                    chiTietHoaDonRepository.save(ct);
                    fromTotal = fromTotal.add(ct.getThanhTien());

                    // Create new item for target
                    com.example.demo.entity.ChiTietHoaDon newCt = new com.example.demo.entity.ChiTietHoaDon();
                    newCt.setHoaDon(toHd);
                    newCt.setThucDon(ct.getThucDon());
                    newCt.setSoLuong(splitQty);
                    newCt.setGiaTaiThoiDiemBan(ct.getGiaTaiThoiDiemBan());
                    newCt.setThanhTien(ct.getGiaTaiThoiDiemBan().multiply(new BigDecimal(splitQty)));
                    toHd.getChiTietHoaDons().add(newCt);
                    chiTietHoaDonRepository.save(newCt);
                    toTotal = toTotal.add(newCt.getThanhTien());

                } else if (splitQty >= ct.getSoLuong()) {
                    // Move entire item to target
                    ct.setHoaDon(toHd);
                    toHd.getChiTietHoaDons().add(ct);
                    itemsToRemove.add(ct);
                    toTotal = toTotal.add(ct.getThanhTien());
                } else {
                    // Keep entire item in source
                    fromTotal = fromTotal.add(ct.getThanhTien());
                }
            }

            // Remove moved items from source invoice
            fromHd.getChiTietHoaDons().removeAll(itemsToRemove);
        }

        // Update totals
        fromHd.setTongTien(fromTotal);
        hoaDonRepository.save(fromHd);

        toHd.setTongTien(toTotal);
        hoaDonRepository.save(toHd);

        // Update table statuses
        toBan.setTinhTrang(com.example.demo.enums.TinhTrangBan.DANG_SU_DUNG);
        banRepository.save(toBan);
    }
}



