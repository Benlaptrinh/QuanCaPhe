package com.example.demo.service;

import com.example.demo.dto.ReportRowDTO;
import com.example.demo.dto.StaffReportRowDTO;
import java.time.LocalDate;
import java.util.List;
import com.example.demo.dto.SalesByDayRowDTO;

public interface ReportService {
    List<ReportRowDTO> thongKeThuChi(LocalDate from, LocalDate to);
    List<StaffReportRowDTO> thongKeNhanVien();
    List<SalesByDayRowDTO> reportSalesByDay(LocalDate from, LocalDate to);
}


