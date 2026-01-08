package com.example.demo.report.service;

import com.example.demo.report.dto.ReportRowDTO;
import com.example.demo.report.dto.StaffReportRowDTO;
import com.example.demo.report.dto.SalesByDayRowDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ReportRowDTO> thongKeThuChi(LocalDate from, LocalDate to);
    List<StaffReportRowDTO> thongKeNhanVien();
    List<SalesByDayRowDTO> reportSalesByDay(LocalDate from, LocalDate to);
}


