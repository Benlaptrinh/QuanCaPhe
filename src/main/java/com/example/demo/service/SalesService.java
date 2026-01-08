package com.example.demo.service;

import com.example.demo.entity.Ban;
import com.example.demo.entity.HoaDon;
import com.example.demo.entity.ThucDon;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.time.LocalDateTime;
import java.math.BigDecimal;

public interface SalesService {
    List<Ban> findAllTables();
    Optional<HoaDon> findUnpaidInvoiceByTable(Long tableId);
    List<ThucDon> findMenuItems();
    void addItemToInvoice(Long tableId, Long itemId, Integer quantity);
    void payInvoice(Long tableId, BigDecimal tienKhach, boolean releaseTable);
    void reserveTable(Long banId, String tenKhach, String sdt, LocalDateTime ngayGioDat);
    void saveSelectedMenu(Long banId, Map<String,String> params);
    void cancelInvoice(Long banId);
    Optional<HoaDon> findInvoiceById(Long id);

    
    void moveTable(Long fromBanId, Long toBanId);
    List<Ban> findEmptyTables();
    List<Ban> findMergeCandidates(Long excludeBanId);
    void mergeTables(Long targetBanId, Long sourceBanId);
    void splitTable(Long fromBanId, Long toBanId, Map<Long, Integer> itemQuantities);
    void cancelReservation(Long banId);
}


