package com.example.raporkayit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor

public class AnaRaporTuruResponse {
    private final UUID anaRaporTuruId;
    private final String anaRaporTuruKodu;
    private final String anaRaporTuruAdi;
}
