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
public class ChiTietThucDonId implements Serializable {

    @Column(name = "ma_thuc_don")
    private Long maThucDon;

    @Column(name = "ma_hang_hoa")
    private Long maHangHoa;
}


