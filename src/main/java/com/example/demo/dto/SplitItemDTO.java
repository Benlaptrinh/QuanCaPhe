package com.example.demo.dto;

public class SplitItemDTO {
    private Long maThucDon;
    private Integer soLuong;

    public SplitItemDTO() {}

    public SplitItemDTO(Long maThucDon, Integer soLuong) {
        this.maThucDon = maThucDon;
        this.soLuong = soLuong;
    }

    public Long getMaThucDon() {
        return maThucDon;
    }

    public void setMaThucDon(Long maThucDon) {
        this.maThucDon = maThucDon;
    }

    public Integer getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Integer soLuong) {
        this.soLuong = soLuong;
    }
}


