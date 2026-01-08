package com.example.demo.report.service.impl;

import com.example.demo.report.dto.ReportRowDTO;
import com.example.demo.report.dto.StaffReportRowDTO;
import com.example.demo.report.dto.SalesByDayRowDTO;
import com.example.demo.repository.HoaDonRepository;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.report.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;

import com.example.demo.entity.HoaDon;

@Service
public class ReportServiceImpl implements ReportService {

    private final HoaDonRepository hoaDonRepository;
    private final NhanVienRepository nhanVienRepository;

    public ReportServiceImpl(HoaDonRepository hoaDonRepository, NhanVienRepository nhanVienRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.nhanVienRepository = nhanVienRepository;
    }

    @Override
    public List<ReportRowDTO> thongKeThuChi(LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(23, 59, 59);

        List<Object[]> rows = hoaDonRepository.thongKeThuRaw(fromTime, toTime);
        List<ReportRowDTO> result = rows.stream()
                .map(r -> new ReportRowDTO(
                        (java.sql.Date) r[0],
                        (BigDecimal) r[1],
                        (Number) r[2]
                ))
                .toList();

        return result;
    }

    @Override
    public List<StaffReportRowDTO> thongKeNhanVien() {
        List<Object[]> rows = nhanVienRepository.thongKeNhanVienRaw();
    
        List<StaffReportRowDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            Boolean enabled = (Boolean) r[0];
            Long count = (Long) r[1];
    
            String trangThai = enabled ? "Đang làm" : "Nghỉ việc";
            result.add(new StaffReportRowDTO(trangThai, count));
        }
        return result;
    }
    

    @Override
    public List<SalesByDayRowDTO> reportSalesByDay(LocalDate from, LocalDate to) {
        LocalDateTime fromTime = from.atStartOfDay();
        LocalDateTime toTime = to.atTime(23, 59, 59);

        List<Object[]> rows = hoaDonRepository.thongKeBanHangTheoNgayRaw(fromTime, toTime);
        return rows.stream()
                .map(r -> new SalesByDayRowDTO(
                        ((java.sql.Date) r[0]).toLocalDate(),
                        ((Number) r[1]).longValue(),
                        (BigDecimal) r[2]
                ))
                .toList();
    }
}


