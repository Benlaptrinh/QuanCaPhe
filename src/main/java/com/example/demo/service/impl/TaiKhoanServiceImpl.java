package com.example.demo.service.impl;

import com.example.demo.entity.TaiKhoan;
import com.example.demo.repository.TaiKhoanRepository;
import com.example.demo.service.TaiKhoanService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaiKhoanServiceImpl implements TaiKhoanService {

	private final TaiKhoanRepository taiKhoanRepository;
	private final PasswordEncoder passwordEncoder;

	public TaiKhoanServiceImpl(TaiKhoanRepository taiKhoanRepository,
	                          PasswordEncoder passwordEncoder) {
		this.taiKhoanRepository = taiKhoanRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public TaiKhoan save(TaiKhoan taiKhoan) {
		// validate username uniqueness
		if (taiKhoan.getTenDangNhap() != null && !taiKhoan.getTenDangNhap().isEmpty()) {
			taiKhoanRepository.findByTenDangNhap(taiKhoan.getTenDangNhap())
					.ifPresent(existing -> {
						// if creating new or changing to a username that belongs to another account -> error
						if (taiKhoan.getMaTaiKhoan() == null || !existing.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
							throw new IllegalArgumentException("Username already exists");
						}
					});
		}

		// If creating a new account, password is required
		if (taiKhoan.getMaTaiKhoan() == null) {
			if (taiKhoan.getMatKhau() == null || taiKhoan.getMatKhau().isBlank()) {
				throw new IllegalArgumentException("Password required when creating account");
			}
		}

		if (taiKhoan.getMatKhau() != null && !taiKhoan.getMatKhau().isEmpty()) {
			// Chỉ mã hóa nếu chưa mã hóa (chuỗi chưa bắt đầu bằng $2a$ hoặc $2b$)
			if (!taiKhoan.getMatKhau().startsWith("$2a$") && !taiKhoan.getMatKhau().startsWith("$2b$")) {
				taiKhoan.setMatKhau(passwordEncoder.encode(taiKhoan.getMatKhau()));
			}
		}

		return taiKhoanRepository.save(taiKhoan);
	}

	@Override
	public List<TaiKhoan> findAll() {
		return taiKhoanRepository.findAll();
	}

	@Override
	public Optional<TaiKhoan> findById(Long id) {
		return taiKhoanRepository.findById(id);
	}

	@Override
	public Optional<TaiKhoan> findByUsername(String username) {
		return taiKhoanRepository.findByTenDangNhap(username);
	}

	@Override
	public void disable(Long id) {
		taiKhoanRepository.findById(id).ifPresent(t -> {
			t.setEnabled(false);
			taiKhoanRepository.save(t);
		});
	}
}


