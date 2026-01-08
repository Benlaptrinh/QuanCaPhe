package com.example.demo.report.service;

import com.example.demo.report.dto.StaffReportRowDTO;

import java.util.List;

public interface StaffReportService {
    List<StaffReportRowDTO> getStaffSummary();
}


