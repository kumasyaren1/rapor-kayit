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
@Table(name = "rapor_turu")
public class RaporTuru {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rapor_turu_id",
            nullable = false,
            updatable = false)
    private UUID raporTuruId;
    @Column(name = "rapor_turu_kodu",
            nullable = false,
            unique = true,
            length = 30)
    private String raporTuruKodu;

    @Column(name = "rapor_turu_adi",
            nullable = false,
            length = 100)
    private String raporTuruAdi;

    @Column(name = "aktif",
            nullable = false)
    private Boolean aktif;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) //RaporTuru veritabanından getirildiğinde bağlı AnaRaporTuru verisinin hemen yüklenmesini zorunlu kılmaz. İhtiyaç olduğunda yüklenmesini sağlar.
    @JoinColumn(name = "ana_rapor_turu_id", nullable = false) //FK
    private AnaRaporTuru anaRaporTuru;
}
