package com.example.raporkayit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vergi_kodu")
public class VergiKodu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vergi_kodu_id",
            nullable = false,
            updatable = false)
    private UUID vergiKoduId;
    @Column(name = "vergi_kodu" ,
            nullable = false,
            unique = true,
            length = 30)
    private String vergiKodu;
    @Column(name = "vergi_kodu_adi",
            nullable = false,
            length = 30)
    private String vergiKoduAdi;
    @Column(name = "aktif",
            nullable = false)
    private Boolean aktif;
}
