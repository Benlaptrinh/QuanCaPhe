package com.example.demo.report.service.impl;

import com.example.demo.report.dto.ReportRowDTO;
import com.example.demo.report.service.FinanceReportService;
import com.example.demo.repository.HoaDonRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@Service
public class FinanceReportServiceImpl implements FinanceReportService {

    private final HoaDonRepository hoaDonRepository;

    public FinanceReportServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
    }

    @Override
    public List<ReportRowDTO> getFinanceReport(LocalDate from, LocalDate to) {
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
}


