package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.service.SalesService;
import com.example.demo.service.ThietBiService;
import com.example.demo.service.ThucDonService;
import com.example.demo.service.HangHoaService;
import com.example.demo.service.KhuyenMaiService;
import com.example.demo.repository.DonViTinhRepository;
import com.example.demo.service.NhanVienService;
import com.example.demo.repository.TaiKhoanRepository;
import java.security.Principal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo.repository.HangHoaRepository;

@Controller
@RequestMapping("/admin")
public class AdminPagesController {

    private final SalesService salesService;
    private final ThietBiService thietBiService;
    private final HangHoaService hangHoaService;
    private final DonViTinhRepository donViTinhRepository;
    private final NhanVienService nhanVienService;
    private final TaiKhoanRepository taiKhoanRepository;
    private final HangHoaRepository hangHoaRepository;
    private final ThucDonService thucDonService;
    private final KhuyenMaiService khuyenMaiService;
    private String sidebar = "fragments/sidebar-admin";

    public AdminPagesController(SalesService salesService, ThietBiService thietBiService, HangHoaService hangHoaService,
                                DonViTinhRepository donViTinhRepository, NhanVienService nhanVienService,
                                TaiKhoanRepository taiKhoanRepository, HangHoaRepository hangHoaRepository,
                                ThucDonService thucDonService, KhuyenMaiService khuyenMaiService) {
        this.salesService = salesService;
        this.thietBiService = thietBiService;
        this.hangHoaService = hangHoaService;
        this.donViTinhRepository = donViTinhRepository;
        this.nhanVienService = nhanVienService;
        this.taiKhoanRepository = taiKhoanRepository;
        this.hangHoaRepository = hangHoaRepository;
        this.thucDonService = thucDonService;
        this.khuyenMaiService = khuyenMaiService;
    }

    private String usernameFromAuth(Authentication auth) {
        return auth == null ? "anonymous" : auth.getName();
    }

    @GetMapping("/sales")
    public String sales(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "sales/index");
        model.addAttribute("tables", salesService.findAllTables());
        return "layout/base";
    }

    @GetMapping("/equipment")
    public String equipment(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/equipment");
        model.addAttribute("items", thietBiService.findAll());
        // form backing object; prefer flash attribute if redirected from edit
        if (!model.containsAttribute("thietBi")) {
            model.addAttribute("thietBi", new com.example.demo.entity.ThietBi());
        }
        return "layout/base";
    }

    @GetMapping("/warehouse")
    public String warehouse(@org.springframework.web.bind.annotation.RequestParam(required = false) String keyword, Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/kho/list");
        model.addAttribute("items", hangHoaService.searchHangHoa(keyword));
        model.addAttribute("keyword", keyword);
        model.addAttribute("form", new com.example.demo.dto.HangHoaNhapForm());
        model.addAttribute("donViTinhs", donViTinhRepository.findAll());
        model.addAttribute("activeMenu", "warehouse");
        model.addAttribute("xuatForm", new com.example.demo.dto.XuatHangForm());
        model.addAttribute("hangHoas", hangHoaService.getDanhSachKho());
        return "layout/base";
    }

    @GetMapping("/warehouse/create")
    public String warehouseCreateForm(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/kho/form");
        model.addAttribute("form", new com.example.demo.dto.HangHoaNhapForm());
        model.addAttribute("donViTinhs", donViTinhRepository.findAll());
        model.addAttribute("activeMenu", "warehouse");
        return "layout/base";
    }

    @PostMapping("/warehouse/create")
    public String warehouseCreateSubmit(@ModelAttribute("form") com.example.demo.dto.HangHoaNhapForm form,
                                        Principal principal, RedirectAttributes ra) {
        String username = principal == null ? null : principal.getName();
        com.example.demo.entity.NhanVien nv = null;
        if (username != null) {
            com.example.demo.entity.TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username).orElse(null);
            if (tk != null) {
                nv = nhanVienService.findByTaiKhoanId(tk.getMaTaiKhoan()).orElse(null);
            }
        }

        hangHoaService.nhapHang(form, nv);
        ra.addFlashAttribute("success", "Nhập hàng thành công");
        return "redirect:/admin/warehouse";
    }

    @PostMapping("/warehouse/export")
    public String warehouseExportSubmit(@ModelAttribute("xuatForm") com.example.demo.dto.XuatHangForm xuatForm,
                                        Principal principal, RedirectAttributes ra) {
        String username = principal == null ? null : principal.getName();
        com.example.demo.entity.NhanVien nv = null;
        if (username != null) {
            com.example.demo.entity.TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username).orElse(null);
            if (tk != null) {
                nv = nhanVienService.findByTaiKhoanId(tk.getMaTaiKhoan()).orElse(null);
            }
        }

        hangHoaService.xuatHang(xuatForm.getHangHoaId(), xuatForm.getSoLuong(), xuatForm.getNgayXuat(), nv);
        ra.addFlashAttribute("success", "Xuất hàng thành công");
        return "redirect:/admin/warehouse";
    }

    @GetMapping("/warehouse/edit/{id}")
    public String warehouseEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        com.example.demo.entity.HangHoa hh = hangHoaRepository.findById(id).orElse(null);
        if (hh == null) {
            ra.addFlashAttribute("error", "Không tìm thấy hàng hóa");
            return "redirect:/admin/warehouse";
        }
        com.example.demo.dto.EditHangHoaForm form = new com.example.demo.dto.EditHangHoaForm();
        form.setId(hh.getMaHangHoa());
        form.setTenHangHoa(hh.getTenHangHoa());
        form.setSoLuong(hh.getSoLuong());
        form.setDonGia(hh.getDonGia());
        if (hh.getDonViTinh() != null) {
            form.setDonViTinhId(hh.getDonViTinh().getMaDonViTinh());
        }
        model.addAttribute("editForm", form);
        model.addAttribute("donViTinhs", donViTinhRepository.findAll());
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/kho/edit");
        model.addAttribute("activeMenu", "warehouse");
        return "layout/base";
    }

    @PostMapping("/warehouse/edit")
    public String warehouseEditSubmit(@ModelAttribute("editForm") com.example.demo.dto.EditHangHoaForm form,
                                      RedirectAttributes ra) {
        hangHoaService.updateHangHoa(form);
        ra.addFlashAttribute("success", "Cập nhật hàng hóa thành công");
        return "redirect:/admin/warehouse";
    }

    @GetMapping("/warehouse/delete/{id}")
    public String warehouseDelete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            hangHoaService.deleteHangHoa(id);
            ra.addFlashAttribute("success", "Xóa hàng hóa thành công");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/warehouse";
    }

    @GetMapping("/menu")
    public String menu(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/menu");
        model.addAttribute("menuList", thucDonService.findAll());
        return "layout/base";
    }

    @GetMapping("/menu/search")
    public String menuSearch(@org.springframework.web.bind.annotation.RequestParam(required = false) String keyword,
                              Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/menu");
        java.util.List<com.example.demo.entity.ThucDon> list = thucDonService.searchByTenMon(keyword);
        if (list.isEmpty()) {
            model.addAttribute("error", "Không có trong cơ sở dữ liệu");
        }
        model.addAttribute("menuList", list);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeMenu", "menu");
        return "layout/base";
    }

    @GetMapping("/menu/create")
    public String menuCreateForm(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/menu/create");
        model.addAttribute("activeMenu", "menu");
        return "layout/base";
    }

    @PostMapping("/menu/create")
    public String menuCreateSubmit(@org.springframework.web.bind.annotation.RequestParam String tenMon,
                                   @org.springframework.web.bind.annotation.RequestParam java.math.BigDecimal giaTien,
                                   RedirectAttributes redirect) {
        try {
            thucDonService.create(tenMon, giaTien);
            redirect.addFlashAttribute("success", "Thêm món thành công");
            return "redirect:/admin/menu";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/menu/create";
        }
    }

    @GetMapping("/menu/edit/{id}")
    public String menuEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        java.util.Optional<com.example.demo.entity.ThucDon> opt = thucDonService.findById(id);
        if (opt.isEmpty()) {
            ra.addFlashAttribute("error", "Không tìm thấy món");
            return "redirect:/admin/menu";
        }
        model.addAttribute("menu", opt.get());
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/menu/edit");
        model.addAttribute("activeMenu", "menu");
        return "layout/base";
    }

    @PostMapping("/menu/edit")
    public String menuEditSubmit(@org.springframework.web.bind.annotation.RequestParam Long id,
                                 @org.springframework.web.bind.annotation.RequestParam String tenMon,
                                 @org.springframework.web.bind.annotation.RequestParam java.math.BigDecimal giaTien,
                                 RedirectAttributes redirect) {
        try {
            thucDonService.update(id, tenMon, giaTien);
            redirect.addFlashAttribute("success", "Cập nhật thành công");
            return "redirect:/admin/menu";
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/menu/edit/" + id;
        }
    }

    @GetMapping("/menu/delete/{id}")
    public String menuDelete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            thucDonService.deleteById(id);
            redirect.addFlashAttribute("success", "Xóa danh mục thành công");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/menu";
    }

    @GetMapping("/marketing")
    public String marketing(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/marketing/list");
        model.addAttribute("khuyenMais", khuyenMaiService.getAllKhuyenMai());
        model.addAttribute("activeMenu", "marketing");
        return "layout/base";
    }

    @GetMapping("/budget")
    public String budget(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/budget");
        return "layout/base";
    }

    @GetMapping("/reports")
    public String reports(Model model, Authentication auth) {
        model.addAttribute("username", usernameFromAuth(auth));
        model.addAttribute("sidebarFragment", sidebar);
        model.addAttribute("contentFragment", "admin/reports");
        return "layout/base";
    }
}
