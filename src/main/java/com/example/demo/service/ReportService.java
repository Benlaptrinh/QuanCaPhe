package com.example.demo.service;

import com.example.demo.dto.ReportRowDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<ReportRowDTO> thongKeThuChi(LocalDate from, LocalDate to);
}


