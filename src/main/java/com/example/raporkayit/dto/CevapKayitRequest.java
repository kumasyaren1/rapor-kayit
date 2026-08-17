package com.example.raporkayit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CevapKayitRequest {
    @NotBlank
    private String cevapNumarasi;

    @NotNull
    private LocalDate cevapTarihi;

    @NotBlank
    private String cevapSonucu;
}