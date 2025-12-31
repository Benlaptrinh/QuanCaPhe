package com.example.demo.entity;

import com.example.demo.enums.TinhTrangBan;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ban")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long maBan;

    @Enumerated(EnumType.STRING)
    private TinhTrangBan tinhTrang;

    private String tenBan;
}


