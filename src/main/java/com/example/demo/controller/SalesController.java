package com.example.demo.controller;

import com.example.demo.service.SalesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;

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
        java.util.Optional<com.example.demo.entity.HoaDon> hoaDonOpt = salesService.findUnpaidInvoiceByTable(banId);
        model.addAttribute("hoaDon", hoaDonOpt.orElse(null));
        java.util.Map<Long, Integer> qtyMap = new java.util.HashMap<>();
        hoaDonOpt.ifPresent(hd -> {
            if (hd.getChiTietHoaDons() != null) {
                for (com.example.demo.entity.ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
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
        // same as selectMenuForm but separate path to avoid ambiguity
        model.addAttribute("menu", salesService.findMenuItems());
        model.addAttribute("tableId", banId);
        java.util.Optional<com.example.demo.entity.HoaDon> hoaDonOpt = salesService.findUnpaidInvoiceByTable(banId);
        model.addAttribute("hoaDon", hoaDonOpt.orElse(null));
        java.util.Map<Long, Integer> qtyMap = new java.util.HashMap<>();
        hoaDonOpt.ifPresent(hd -> {
            if (hd.getChiTietHoaDons() != null) {
                for (com.example.demo.entity.ChiTietHoaDon ct : hd.getChiTietHoaDons()) {
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
                                   @RequestParam java.util.Map<String,String> params) {
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
        java.math.BigDecimal money;
        try {
            money = new java.math.BigDecimal(tienKhach.replaceAll("[^0-9.]", ""));
        } catch (Exception ex) {
            money = java.math.BigDecimal.ZERO;
        }
        // determine release: default to true when full payment
        java.util.Optional<com.example.demo.entity.HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(banId);
        boolean release = false;
        if (hdOpt.isPresent()) {
            com.example.demo.entity.HoaDon hd = hdOpt.get();
            java.math.BigDecimal total = hd.getTongTien() == null ? java.math.BigDecimal.ZERO : hd.getTongTien();
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
        java.math.BigDecimal money;
        try {
            money = new java.math.BigDecimal(tienKhach.replaceAll("[^0-9.]", ""));
        } catch (Exception ex) {
            money = java.math.BigDecimal.ZERO;
        }
        try {
            java.util.Optional<com.example.demo.entity.HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(banId);
            if (hdOpt.isEmpty()) {
                return "ERROR:Không tìm thấy hóa đơn để thanh toán";
            }
            com.example.demo.entity.HoaDon hd = hdOpt.get();
            java.math.BigDecimal total = hd.getTongTien() == null ? java.math.BigDecimal.ZERO : hd.getTongTien();
            boolean release = money.compareTo(total) >= 0;
            salesService.payInvoice(banId, money, release);
            if (print) {
                // return indicator for client to open print page; use the paid invoice id
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
        java.util.Optional<com.example.demo.entity.HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(id);
        model.addAttribute("banId", id);
        if (hdOpt.isEmpty()) {
            model.addAttribute("empty", true);
        } else {
            model.addAttribute("empty", false);
            com.example.demo.entity.HoaDon hd = hdOpt.get();
            model.addAttribute("hoaDon", hd);
            model.addAttribute("details", hd.getChiTietHoaDons());
        }
        return "sales/fragments/view-ban :: content";
    }

    @GetMapping("/ban/{id}/reserve")
    public String reserveBanFragment(@PathVariable("id") Long id, Model model) {
        model.addAttribute("banId", id);
        // provide minimum datetime for datetime-local input to avoid using T(...) in template
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        String min = now.truncatedTo(java.time.temporal.ChronoUnit.MINUTES).toString().replace(":", "%3A");
        // format to yyyy-MM-dd'T'HH:mm for input; replace %3A back to ':' for safety
        String minFormatted = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").format(now);
        model.addAttribute("minDate", minFormatted);
        return "sales/fragments/reserve :: content";
    }

    @PostMapping("/ban/{id}/reserve")
    @ResponseBody
    public String reserveBanSubmit(@PathVariable("id") Long id,
                                   @RequestParam("tenKhach") String tenKhach,
                                   @RequestParam("sdt") String sdt,
                                   @RequestParam("ngayGio") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime ngayGio) {
        try {
            salesService.reserveTable(id, tenKhach, sdt, ngayGio);
            return "OK";
        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    // GET modal for move - returns fragment listing empty tables (excluding from)
    @GetMapping("/ban/{fromBanId}/move")
    public String moveBanFragment(@PathVariable("fromBanId") Long fromBanId, Model model) {
        model.addAttribute("fromBanId", fromBanId);
        java.util.List<com.example.demo.entity.Ban> empties = salesService.findEmptyTables();
        // exclude source table if present
        empties.removeIf(b -> b.getMaBan().equals(fromBanId));
        model.addAttribute("available", empties);
        return "sales/fragments/move-ban :: content";
    }

    // JSON API to perform move
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

    // GET modal for merge - returns fragment listing candidate source tables (excluding target)
    @GetMapping("/ban/{targetBanId}/merge")
    public String mergeBanFragment(@PathVariable("targetBanId") Long targetBanId, Model model) {
        model.addAttribute("targetBanId", targetBanId);
        model.addAttribute("candidates", salesService.findMergeCandidates(targetBanId));
        return "sales/fragments/merge-ban :: content";
    }

    // GET modal for split - returns fragment for splitting a table
    @GetMapping("/ban/{fromBanId}/split")
    public String splitBanFragment(@PathVariable("fromBanId") Long fromBanId, Model model) {
        java.util.Optional<com.example.demo.entity.HoaDon> hdOpt = salesService.findUnpaidInvoiceByTable(fromBanId);
        if (hdOpt.isEmpty()) {
            model.addAttribute("error", "Bàn không có hóa đơn để tách");
            return "sales/fragments/view-ban :: content";
        }
        model.addAttribute("fromBanId", fromBanId);
        model.addAttribute("hoaDon", hdOpt.get());
        model.addAttribute("empties", salesService.findEmptyTables());
        return "sales/fragments/split-ban :: content";
    }

    // POST split via form params (legacy)
    @PostMapping("/ban/{fromBanId}/split")
    @ResponseBody
    public String splitTable(@PathVariable("fromBanId") Long fromBanId,
                             @RequestParam("toBanId") Long toBanId,
                             @RequestParam java.util.Map<String, String> params) {
        try {
            java.util.Map<Long, Integer> itemQuantities = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, String> entry : params.entrySet()) {
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

    // Cancel reservation
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

    // JSON API for split
    @PostMapping("/{fromBanId}/split")
    @ResponseBody
    public ResponseEntity<String> splitTableJson(@PathVariable("fromBanId") Long fromBanId, @RequestBody Map<String, Object> payload) {
        try {
            if (payload == null || !payload.containsKey("toBanId") || !payload.containsKey("items")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing toBanId or items");
            }
            Long toBanId = ((Number) payload.get("toBanId")).longValue();
            java.util.List<?> items = (java.util.List<?>) payload.get("items");
            java.util.Map<Long, Integer> itemQuantities = new java.util.HashMap<>();
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

    // POST perform merge (form submit)
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

    // JSON API to perform merge
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


