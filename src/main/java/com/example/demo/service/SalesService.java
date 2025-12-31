package com.example.demo.service;

import com.example.demo.entity.Ban;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.ThucDon;

import java.util.List;
import java.util.Optional;

public interface SalesService {
    List<Ban> findAllTables();
    Optional<HoaDon> findUnpaidInvoiceByTable(Long tableId);
    List<ThucDon> findMenuItems();
    void addItemToInvoice(Long tableId, Long itemId, Integer quantity);
    void payInvoice(Long tableId, java.math.BigDecimal tienKhach, boolean releaseTable);
    void reserveTable(Long banId, String tenKhach, String sdt, java.time.LocalDateTime ngayGioDat);
    void saveSelectedMenu(Long banId, java.util.Map<String,String> params);
    void cancelInvoice(Long banId);
    java.util.Optional<com.example.demo.entity.HoaDon> findInvoiceById(Long id);
}


