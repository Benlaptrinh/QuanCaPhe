package com.example.demo.security;

import com.example.demo.entity.TaiKhoan;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CustomUserDetails implements UserDetails {

    private final TaiKhoan taiKhoan;

    public CustomUserDetails(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (taiKhoan.getQuyenHan() == null) return List.of();
        return List.of(new SimpleGrantedAuthority("ROLE_" + taiKhoan.getQuyenHan().name()));
    }

    @Override
    public String getPassword() {
        return taiKhoan.getMatKhau();
    }

    @Override
    public String getUsername() {
        return taiKhoan.getTenDangNhap();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return taiKhoan.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return taiKhoan.isEnabled();
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomUserDetails)) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }
}


