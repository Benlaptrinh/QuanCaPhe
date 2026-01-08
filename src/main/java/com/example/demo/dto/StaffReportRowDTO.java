package com.example.demo.dto;

public class StaffReportRowDTO {

    private String trangThai;
    private Long soLuong;

    public StaffReportRowDTO(String trangThai, Long soLuong) {
        this.trangThai = trangThai;
        this.soLuong = soLuong == null ? 0L : soLuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public Long getSoLuong() {
        return soLuong;
    }
}


