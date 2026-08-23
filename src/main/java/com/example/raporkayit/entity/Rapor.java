package com.example.raporkayit.entity;


import com.example.raporkayit.Enum.RaporDurumu;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rapor")
public class Rapor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "rapor_id",
             nullable = false,
             updatable = false)
    private UUID raporId;

    @Column (name = "rapor_kayit_no",
             nullable = false,
             unique = true,
             length = 30)
    private String raporKayitNo;

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

    @Column (name = "duzenleme_tarihi",
             nullable = false)
    private LocalDate duzenlemeTarihi;

    @Column (name = "aciklama",
             length = 255)
    private String aciklama;

    @Enumerated(EnumType.STRING)
    @Column(name = "durum", nullable = false, length = 30)
    private RaporDurumu durum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rapor_turu_id", nullable = false)
    private RaporTuru raporTuru;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vergi_kodu_id", nullable = false)
    private VergiKodu vergiKodu;
}
