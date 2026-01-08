package com.example.demo.service.impl;

import com.example.demo.dto.ReportRowDTO;
import com.example.demo.repository.HoaDonRepository;
import com.example.demo.service.ReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.math.BigDecimal;

import com.example.demo.entity.HoaDon;
import com.example.demo.dto.SalesByDayRowDTO;
import com.example.demo.repository.NhanVienRepository;
import com.example.demo.dto.StaffReportRowDTO;

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
    
        // Use native query that aggregates by date and map results to DTO
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
        return nhanVienRepository.thongKeNhanVien();
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
                        (java.math.BigDecimal) r[2]
                ))
                .toList();
    }
    
}


