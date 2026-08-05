package com.example.raporkayit.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mukellef")
public class Mukellef {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "mukellef_id",
            nullable = false,
            updatable = false)
    private UUID mukellefId;

    @Column (name = "vergi_kimlik_no",
            length = 10)
    private String vergiKimlikNo;

    @Column (name = "tc_kimlik_no",
            length = 11)
    private String tcKimlikNo;

    @Column (name = "ad_soyad_unvan",
            nullable = false,
            length = 100)
    private String adSoyadUnvan;

    @Column(name = "aktif",
            nullable = false)
    private Boolean aktif;
}
