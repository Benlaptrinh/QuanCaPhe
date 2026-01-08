package com.example.demo.report.service;

import com.example.demo.report.dto.ReportRowDTO;

import java.time.LocalDate;
import java.util.List;

public interface FinanceReportService {
    List<ReportRowDTO> getFinanceReport(LocalDate from, LocalDate to);
}


