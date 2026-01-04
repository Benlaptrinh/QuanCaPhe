package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ThietBiDTO {
    private Long id;

    @NotBlank(message = "Tên thiết bị là bắt buộc")
    private String ten;

    @NotNull(message = "Ngày mua là bắt buộc")
    private LocalDate ngayMua;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer soLuong;

    @NotNull(message = "Đơn giá là bắt buộc")
    @Min(value = 0, message = "Đơn giá phải >= 0")
    private BigDecimal donGia;

    // read-only computed
    private BigDecimal gia;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public LocalDate getNgayMua() { return ngayMua; }
    public void setNgayMua(LocalDate ngayMua) { this.ngayMua = ngayMua; }
    public Integer getSoLuong() { return soLuong; }
    public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }
    public BigDecimal getDonGia() { return donGia; }
    public void setDonGia(BigDecimal donGia) { this.donGia = donGia; }
    public BigDecimal getGia() { return gia; }
    public void setGia(BigDecimal gia) { this.gia = gia; }
}


