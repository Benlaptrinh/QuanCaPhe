package com.example.demo.entity.id;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.*;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChiTietDatBanId implements Serializable {

    @Column(name = "ma_ban")
    private Long maBan;

    @Column(name = "ngay_gio_dat")
    private LocalDateTime ngayGioDat;
}


