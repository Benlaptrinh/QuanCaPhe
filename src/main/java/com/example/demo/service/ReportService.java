package com.example.demo.service;

import com.example.demo.dto.ReportRowDTO;
import com.example.demo.dto.StaffReportRowDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ReportRowDTO> thongKeThuChi(LocalDate from, LocalDate to);
    List<StaffReportRowDTO> thongKeNhanVien();
    List<com.example.demo.dto.SalesByDayRowDTO> reportSalesByDay(LocalDate from, LocalDate to);
}


