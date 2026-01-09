package com.example.demo.entity.id;

import java.io.Serializable;
import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietHoaDonId implements Serializable {

    @Column(name = "ma_hoa_don")
    private Long maHoaDon;

    @Column(name = "ma_thuc_don")
    private Long maThucDon;
}


