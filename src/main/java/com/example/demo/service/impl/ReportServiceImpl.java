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
    
        List<HoaDon> hoaDons =
                hoaDonRepository.findHoaDonDaThanhToan(fromTime, toTime);
    
        Map<LocalDate, Long> tongThuTheoNgay = new LinkedHashMap<>();
    
        for (HoaDon hd : hoaDons) {
            LocalDate ngay = hd.getNgayThanhToan().toLocalDate();
            long tien = hd.getTongTien() == null ? 0L : hd.getTongTien().longValue();
    
            tongThuTheoNgay.merge(ngay, tien, Long::sum);
        }
    
        List<ReportRowDTO> result = new ArrayList<>();
        for (var e : tongThuTheoNgay.entrySet()) {
            result.add(new ReportRowDTO(
                    e.getKey(),
                    e.getValue(),
                    0L
            ));
        }
    
        return result;
    }
    
}


