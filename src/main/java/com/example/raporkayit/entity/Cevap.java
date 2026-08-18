package com.example.raporkayit.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;


import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "cevap")
public class Cevap {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cevap_id",
            nullable = false,
            updatable = false)
    private UUID cevapId;

    @Column(name = "cevap_tarihi",
            nullable = false)
    private LocalDate cevapTarihi;

    @Column (name = "cevap_numarasi",
             nullable = false,
             unique = true,
             length = 20)
    private String cevapNumarasi;

    @Column (name = "sonuc",
             nullable = false,
             length = 255)
    private String sonuc;

    @OneToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "rapor_id", nullable = false)
    private Rapor rapor;
}
