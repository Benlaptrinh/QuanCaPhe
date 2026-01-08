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

@Service
public class ReportServiceImpl implements ReportService {

    private final HoaDonRepository hoaDonRepository;

    public ReportServiceImpl(HoaDonRepository hoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
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
    
}


