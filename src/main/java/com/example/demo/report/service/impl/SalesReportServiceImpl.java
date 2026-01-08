package com.example.demo.report.service.impl;

import com.example.demo.report.dto.SalesByDayRowDTO;
import com.example.demo.report.service.SalesReportService;
import com.example.demo.repository.HoaDonRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class SalesReportServiceImpl implements SalesReportService {

    private final HoaDonRepository hoaDonRepository;

    public SalesReportServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    public List<SalesByDayRowDTO> getSalesByDay(LocalDate from, LocalDate to) {
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


