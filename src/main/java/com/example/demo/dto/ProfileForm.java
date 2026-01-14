package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * ProfileForm
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
 * 09-01-2026  Viet    Create
 */
@Getter
@Setter
public class ProfileForm {

    @NotBlank(message = "Ho ten bat buoc")
    @Size(max = 100, message = "Ho ten toi da 100 ky tu")
    private String hoTen;

    @NotBlank(message = "Dia chi bat buoc")
    @Size(max = 10, message = "Dia chi toi da 10 ky tu")
    private String diaChi;

    @NotBlank(message = "So dien thoai bat buoc")
    @Pattern(regexp = "^\\d{9,15}$", message = "So dien thoai chi duoc nhap so (9-15 chu so)")
    private String soDienThoai;
}
