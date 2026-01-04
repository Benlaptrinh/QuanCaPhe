package com.example.demo.dto;

import java.util.List;

public class SplitRequestDTO {
    private Long toBanId;
    private List<SplitItemDTO> items;

    public SplitRequestDTO() {}

    public Long getToBanId() {
        return toBanId;
    }

    public void setToBanId(Long toBanId) {
        this.toBanId = toBanId;
    }

    public List<SplitItemDTO> getItems() {
        return items;
    }

    public void setItems(List<SplitItemDTO> items) {
        this.items = items;
    }
}


