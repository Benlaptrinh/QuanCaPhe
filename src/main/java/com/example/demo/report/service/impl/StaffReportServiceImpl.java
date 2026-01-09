package com.example.demo.report.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.example.demo.report.dto.StaffReportRowDTO;
import com.example.demo.report.service.StaffReportService;
import com.example.demo.repository.NhanVienRepository;
import org.springframework.stereotype.Service;

/**
 * StaffReportServiceImpl
 *
 * Version 1.0
 *
 * Date: 09-01-2026
 *
 * Copyright
 *
 * Modification Logs:
 * DATE        AUTHOR      DESCRIPTION
 * -----------------------------------
 * 09-01-2026  Việt    Create
 */
@Service
public class StaffReportServiceImpl implements StaffReportService {

    private final NhanVienRepository nhanVienRepository;

    /**
     * Creates StaffReportServiceImpl.
     *
     * @param nhanVienRepository nhanVienRepository
     */
    public StaffReportServiceImpl(NhanVienRepository nhanVienRepository) {
        this.nhanVienRepository = nhanVienRepository;
    }

    /**
     * Get staff summary.
     *
     * @return result
     */
    @Override
    public List<StaffReportRowDTO> getStaffSummary() {
        List<Object[]> rows = nhanVienRepository.thongKeNhanVienRaw();
        List<StaffReportRowDTO> result = new ArrayList<>();
        for (Object[] r : rows) {
            Boolean enabled = (Boolean) r[0];
            Number cnt = (Number) r[1];
            String trangThai = enabled != null && enabled ? "Đang làm" : "Nghỉ việc";
            result.add(new StaffReportRowDTO(trangThai, cnt == null ? 0L : cnt.longValue()));
        }
        return result;
    }
}


