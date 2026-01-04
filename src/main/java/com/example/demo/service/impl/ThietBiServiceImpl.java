package com.example.demo.service.impl;

import com.example.demo.entity.ThietBi;
import com.example.demo.repository.ThietBiRepository;
import com.example.demo.service.ThietBiService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ThietBiServiceImpl implements ThietBiService {

    private final ThietBiRepository thietBiRepository;

    public ThietBiServiceImpl(ThietBiRepository thietBiRepository) {
        this.thietBiRepository = thietBiRepository;
    }

    @Override
    public List<ThietBi> findAll() {
        return thietBiRepository.findAll();
    }

    @Override
    public Optional<ThietBi> findById(Long id) {
        return thietBiRepository.findById(id);
    }

    @Override
    public ThietBi save(ThietBi thietBi) {
        return thietBiRepository.save(thietBi);
    }

    @Override
    public void deleteById(Long id) {
        thietBiRepository.deleteById(id);
    }
}
