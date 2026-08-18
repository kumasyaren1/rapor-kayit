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
@Table(name = "tahakkuk")
public class Tahakkuk {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column (name = "tahakkuk_id",
             nullable = false)
    private UUID tahakkukId;

    @Column (name = "tahakkuk_fis_no",
             nullable = false,
             unique = true,
             length = 30)
    private String tahakkukFisNo;

    @Column (name = "tahakkuk_tarihi",
             nullable = false)
    private LocalDate tahakkukTarihi;

    @OneToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "rapor_id",nullable = false)
    private Rapor rapor;
}
