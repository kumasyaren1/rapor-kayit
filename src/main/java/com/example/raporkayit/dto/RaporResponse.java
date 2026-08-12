package com.example.raporkayit.dto;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RaporResponse {
    private final UUID raporId;
    private final String raporKayitNo;

    private final String vergiKimlikNo;
    private final String tcKimlikNo;
    private final String adSoyadUnvan;

    private final LocalDate duzenlemeTarihi;
    private final String aciklama;
    private final String durum;

    private final UUID anaRaporTuruId;
    private final String anaRaporTuruKodu;
    private final String anaRaporTuruAdi;

    private final UUID raporTuruId;
    private final String raporTuruKodu;
    private final String raporTuruAdi;

    private final UUID vergiKoduId;
    private final String vergiKodu;
    private final String vergiKoduAdi;
}
