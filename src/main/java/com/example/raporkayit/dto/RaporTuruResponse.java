package com.example.raporkayit.dto;
import java.util.UUID;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor

public class RaporTuruResponse {
    private final UUID raporTuruId;
    private final String raporTuruKodu;
    private final String raporTuruAdi;
}
