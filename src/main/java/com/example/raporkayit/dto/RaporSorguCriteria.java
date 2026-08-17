package com.example.raporkayit.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class RaporSorguCriteria {
    private String raporKayitNo;
    private String vergiKimlikNo;
    private String tcKimlikNo;
    private String durum;
    private String anaRaporTuruId;
    private String raporTuruId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate baslangicTarihi;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate bitisTarihi;
}