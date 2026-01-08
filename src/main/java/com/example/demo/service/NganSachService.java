package com.example.demo.service;

import com.example.demo.dto.ChiTieuForm;
import com.example.demo.dto.ThuChiDTO;
import java.time.LocalDate;
import java.util.List;

public interface NganSachService {
    List<ThuChiDTO> xemThuChi(LocalDate from, LocalDate to);
    void themChiTieu(ChiTieuForm form, String username);
}


