package com.example.raporkayit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor //Hibernate'in arkada veritabanından okuduğu verileri nesneye dönüştürebilmesi için bu boş kurucu metot zorunludur
@Entity
@Table(name = "ana_rapor_turu")
public class AnaRaporTuru {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ana_rapor_turu_id",
            nullable = false,
            updatable = false)
    private UUID anaRaporTuruId;

    @Column(name = "ana_rapor_turu_kodu",
            nullable = false,
            unique = true,
            length = 30)
    private String anaRaporTuruKodu;

    @Column(name = "ana_rapor_turu_adi",
            nullable = false,
            length = 100)
    private String anaRaporTuruAdi;

    @Column(name = "aktif",
            nullable = false)
    private Boolean aktif;
}
