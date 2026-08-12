package com.example.raporkayit.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class RaporOlusturRequest {

    private String vergiKimlikNo;
    private String tcKimlikNo;

    private UUID anaRaporTuruId;
    private UUID raporTuruId;
    private UUID vergiKoduId;

    @NotNull(message = "Düzenleme tarihi zorunludur.")
    private LocalDate duzenlemeTarihi;
    private String aciklama;

}
