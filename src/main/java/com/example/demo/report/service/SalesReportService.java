package com.example.demo.report.service;

import com.example.demo.report.dto.SalesByDayRowDTO;

import java.time.LocalDate;
import java.util.List;

public interface SalesReportService {
    List<SalesByDayRowDTO> getSalesByDay(LocalDate from, LocalDate to);
}


