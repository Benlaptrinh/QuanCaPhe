package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import com.example.demo.entity.TaiKhoan;
import com.example.demo.enums.Role;
import com.example.demo.repository.TaiKhoanRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner seedAdmin(TaiKhoanRepository taiKhoanRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			boolean adminExists = taiKhoanRepository.findAll()
					.stream()
					.anyMatch(t -> t.getQuyenHan() == Role.ADMIN);
			if (!adminExists) {
				TaiKhoan admin = new TaiKhoan();
				admin.setTenDangNhap("admin");
				admin.setMatKhau(passwordEncoder.encode("admin123"));
				admin.setQuyenHan(Role.ADMIN);
				admin.setEnabled(true);
				taiKhoanRepository.save(admin);
				System.out.println("Seeded admin/admin123");
			}
		};
	}
}
