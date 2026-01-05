package com.example.demo.service;

import com.example.demo.entity.ThietBi;
import java.util.List;
import java.util.Optional;

public interface ThietBiService {
    List<ThietBi> findAll();
    Optional<ThietBi> findById(Long id);
    ThietBi save(ThietBi thietBi);
    void deleteById(Long id);
}


