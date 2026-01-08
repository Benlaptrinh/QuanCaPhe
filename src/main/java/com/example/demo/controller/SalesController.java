package com.example.demo.controller;

import com.example.demo.service.SalesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Map;
import com.example.demo.entity.Ban;
import com.example.demo.entity.ChiTietHoaDon;
import com.example.demo.entity.HoaDon;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sales")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("tables", salesService.findAllTables());
        return "sales/index";
    }

    @GetMapping("/ban/{id}")
    public String viewBan(@PathVariable Long id, Model model) {
        model.addAttribute("hoaDon", salesService.findUnpaidInvoiceByTable(id).orElse(null));
        model.addAttribute("tableId", id);
        return "sales/view-ban";
    }

    @GetMapping("/{banId}/menu")
    public String selectMenuForm(@PathVariable Long banId, Model model) {
        model.addAttribute("menu", salesService.findMenuItems());
        model.addAttribute("tableId", banId);
        Optional<HoaDon> hoaDonOpt = salesService.findUnpaidInvoiceByTable(banId);
        model.addAttribute("hoaDon", hoaDonOpt.orElse(null));
        Map<Long, Integer> qtyMap = new HashMap<>();
        hoaDonOpt.ifPresent(hd -> {
            if (hd.getChiTietHoaDons() != null) {
                for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
                    if (ct.getThucDon() != null && ct.getSoLuong() != null) {
                        qtyMap.put(ct.getThucDon().getMaThucDon(), ct.getSoLuong());
                    }
                }
            }
        });
        model.addAttribute("qtyMap", qtyMap);
        return "sales/fragments/select-menu :: content";
    }

    @GetMapping("/{banId}/menu/modal")
    public String selectMenuModal(@PathVariable Long banId, Model model) {
        
        model.addAttribute("menu", salesService.findMenuItems());
        model.addAttribute("tableId", banId);
        Optional<HoaDon> hoaDonOpt = salesService.findUnpaidInvoiceByTable(banId);
        model.addAttribute("hoaDon", hoaDonOpt.orElse(null));
        Map<Long, Integer> qtyMap = new HashMap<>();
        hoaDonOpt.ifPresent(hd -> {
            if (hd.getChiTietHoaDons() != null) {
                for (ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
                    if (ct.getThucDon() != null && ct.getSoLuong() != null) {
                        qtyMap.put(ct.getThucDon().getMaThucDon(), ct.getSoLuong());
                    }
                }
            }
        });
        model.addAttribute("qtyMap", qtyMap);
        return "sales/fragments/select-menu :: content";
    }

    @GetMapping("/menu")
    public String menuRedirect(@RequestParam("banId") Long banId) {
        return "redirect:/sales/" + banId + "/menu";
    }

    @PostMapping("/{banId}/menu")
    public String selectMenuSubmit(@PathVariable Long banId,
                                   @RequestParam Map<String,String> params) {
        salesService.saveSelectedMenu(banId, params);
        return "redirect:/admin/sales";
    }

    @GetMapping("/{banId}/payment")
    public String paymentForm(@PathVariable Long banId, Model model) {
        model.addAttribute("hoaDon", salesService.findUnpaidInvoiceByTable(banId).orElse(null));
        model.addAttribute("tableId", banId);
        return "sales/payment";
    }

    @GetMapping("/payment")
    public String paymentRedirect(@RequestParam("banId") Long banId) {
        return "redirect:/sales/" + banId + "/payment";
    }

    @PostMapping("/thanh-toan/{banId}")
    public String thanhToan(@PathVariable Long banId,
                            @RequestParam("tienKhach") String tienKhach,
                            @RequestParam(value = "print", required = false) boolean print) {
        BigDecimal money;
        try {
            money = new BigDecimal(tienKhach.replaceAll("[^0-9.]", ""));
        } catch (Exception ex) {
            money = BigDecimal.ZERO;
        }
        
        Optional<HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(banId);
        boolean release = false;
        if (hdOpt.isPresent()) {
            HoaDon hd = hdOpt.get();
            BigDecimal total = hd.getTongTien() == null ? BigDecimal.ZERO : hd.getTongTien();
            if (money.compareTo(total) >= 0) release = true;
        }
        salesService.payInvoice(banId, money, release);
        return "redirect:/admin/sales";
    }

    @GetMapping("/{banId}/payment/modal")
    public String paymentModal(@PathVariable Long banId, Model model) {
        model.addAttribute("tableId", banId);
        model.addAttribute("hoaDon", salesService.findUnpaidInvoiceByTable(banId).orElse(null));
        return "sales/fragments/payment :: content";
    }

    @PostMapping("/{banId}/payment")
    @ResponseBody
    public String paymentSubmit(@PathVariable Long banId,
                                @RequestParam("tienKhach") String tienKhach,
                                @RequestParam(value = "print", required = false) boolean print) {
        BigDecimal money;
        try {
            money = new BigDecimal(tienKhach.replaceAll("[^0-9.]", ""));
        } catch (Exception ex) {
            money = BigDecimal.ZERO;
        }
        try {
            Optional<HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(banId);
            if (hdOpt.isEmpty()) {
                return "ERROR:Không tìm thấy hóa đơn để thanh toán";
            }
            HoaDon hd = hdOpt.get();
            BigDecimal total = hd.getTongTien() == null ? BigDecimal.ZERO : hd.getTongTien();
            boolean release = money.compareTo(total) >= 0;
            salesService.payInvoice(banId, money, release);
            if (print) {
                
                Long id = hd.getMaHoaDon();
                return "OK:PRINT:" + id;
            }
            return "OK";
        } catch (Exception ex) {
            return "ERROR:" + ex.getMessage();
        }
    }

    @PostMapping("/{banId}/cancel")
    public String cancelInvoice(@PathVariable Long banId, RedirectAttributes ra) {
        salesService.cancelInvoice(banId);
        ra.addFlashAttribute("success", "Hủy hóa đơn thành công");
        return "redirect:/admin/sales";
    }

    @GetMapping("/view")
    public String viewRedirect(@RequestParam("banId") Long banId, Model model) {
        return viewBan(banId, model);
    }

    @GetMapping("/ban/{id}/view")
    public String viewBanFragment(@PathVariable("id") Long id, Model model) {
        Optional<HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(id);
        model.addAttribute("banId", id);
        if (hdOpt.isEmpty()) {
            model.addAttribute("empty", true);
        } else {
            model.addAttribute("empty", false);
            HoaDon hd = hdOpt.get();
            model.addAttribute("hoaDon", hd);
            model.addAttribute("details", hd.getChiTietHoaDons());
        }
        return "sales/fragments/view-ban :: content";
    }

    @GetMapping("/ban/{id}/reserve")
    public String reserveBanFragment(@PathVariable("id") Long id, Model model) {
        model.addAttribute("banId", id);
        LocalDateTime now = LocalDateTime.now();
        String min = now.truncatedTo(ChronoUnit.MINUTES).toString().replace(":", "%3A");
        String minFormatted = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").format(now);
        model.addAttribute("minDate", minFormatted);
        return "sales/fragments/reserve :: content";
    }

    @PostMapping("/ban/{id}/reserve")
    @ResponseBody
    public String reserveBanSubmit(@PathVariable("id") Long id,
                                   @RequestParam("tenKhach") String tenKhach,
                                   @RequestParam("sdt") String sdt,
                                   @RequestParam("ngayGio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayGio) {
        try {
            salesService.reserveTable(id, tenKhach, sdt, ngayGio);
            return "OK";
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    
    @GetMapping("/ban/{fromBanId}/move")
    public String moveBanFragment(@PathVariable("fromBanId") Long fromBanId, Model model) {
        model.addAttribute("fromBanId", fromBanId);
        List<Ban> empties = salesService.findEmptyTables();
        
        empties.removeIf(b -> b.getMaBan().equals(fromBanId));
        model.addAttribute("available", empties);
        return "sales/fragments/move-ban :: content";
    }

    
    @PostMapping("/move")
    @ResponseBody
    public ResponseEntity<String> moveTableJson(@RequestBody Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("fromBanId") || !payload.containsKey("toBanId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing fromBanId or toBanId");
            }
            Long fromBanId = ((Number) payload.get("fromBanId")).longValue();
            Long toBanId = ((Number) payload.get("toBanId")).longValue();

            salesService.moveTable(fromBanId, toBanId);
            return ResponseEntity.ok("OK");
        } catch (ClassCastException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid id types");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR:" + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR:" + ex.getMessage());
        }
    }

    
    @GetMapping("/ban/{targetBanId}/merge")
    public String mergeBanFragment(@PathVariable("targetBanId") Long targetBanId, Model model) {
        model.addAttribute("targetBanId", targetBanId);
        model.addAttribute("candidates", salesService.findMergeCandidates(targetBanId));
        return "sales/fragments/merge-ban :: content";
    }

    
    @GetMapping("/ban/{fromBanId}/split")
    public String splitBanFragment(@PathVariable("fromBanId") Long fromBanId, Model model) {
        Optional<HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(fromBanId);
        if (hdOpt.isEmpty()) {
            model.addAttribute("error", "Bàn không có hóa đơn để tách");
            return "sales/fragments/view-ban :: content";
        }
        model.addAttribute("fromBanId", fromBanId);
        model.addAttribute("hoaDon", hdOpt.get());
        model.addAttribute("empties", salesService.findEmptyTables());
        return "sales/fragments/split-ban :: content";
    }

    
    @PostMapping("/ban/{fromBanId}/split")
    @ResponseBody
    public String splitTable(@PathVariable("fromBanId") Long fromBanId,
                             @RequestParam("toBanId") Long toBanId,
                             @RequestParam Map<String, String> params) {
        try {
            Map<Long, Integer> itemQuantities = new HashMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (entry.getKey().startsWith("split_qty_")) {
                    Long itemId = Long.parseLong(entry.getKey().substring(10));
                    Integer qty = Integer.parseInt(entry.getValue());
                    if (qty > 0) itemQuantities.put(itemId, qty);
                }
            }
            salesService.splitTable(fromBanId, toBanId, itemQuantities);
            return "OK";
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    
    @PostMapping("/ban/{banId}/cancel-reservation")
    @ResponseBody
    public ResponseEntity<String> cancelReservation(@PathVariable("banId") Long banId) {
        try {
            salesService.cancelReservation(banId);
            return ResponseEntity.ok("OK");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ERROR:" + ex.getMessage());
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR:" + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR:" + ex.getMessage());
        }
    }

    
    @PostMapping("/{fromBanId}/split")
    @ResponseBody
    public ResponseEntity<String> splitTableJson(@PathVariable("fromBanId") Long fromBanId, @RequestBody Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("toBanId") || !payload.containsKey("items")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing toBanId or items");
            }
            Long toBanId = ((Number) payload.get("toBanId")).longValue();
            List<?> items = (List<?>) payload.get("items");
            Map<Long, Integer> itemQuantities = new HashMap<>();
            for (Object o : items) {
                if (o instanceof Map) {
                    Map m = (Map) o;
                    Long id = ((Number) m.get("thucDonId")).longValue();
                    Integer qty = ((Number) m.get("soLuong")).intValue();
                    if (qty > 0) itemQuantities.put(id, qty);
                }
            }
            salesService.splitTable(fromBanId, toBanId, itemQuantities);
            return ResponseEntity.ok("OK");
        } catch (ClassCastException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payload");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR:" + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR:" + ex.getMessage());
        }
    }

    
    @PostMapping("/ban/{targetBanId}/merge")
    @ResponseBody
    public String mergeTable(@PathVariable Long targetBanId, @RequestParam("sourceBanId") Long sourceBanId) {
        try {
            salesService.mergeTables(targetBanId, sourceBanId);
            return "OK";
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    
    @PostMapping("/merge")
    @ResponseBody
    public ResponseEntity<String> mergeTableJson(@RequestBody Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("targetBanId") || !payload.containsKey("sourceBanId")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing targetBanId or sourceBanId");
            }
            Long targetBanId = ((Number) payload.get("targetBanId")).longValue();
            Long sourceBanId = ((Number) payload.get("sourceBanId")).longValue();

            salesService.mergeTables(targetBanId, sourceBanId);
            return ResponseEntity.ok("OK");
        } catch (ClassCastException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid id types");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("ERROR:" + ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR:" + ex.getMessage());
        }
    }
}

