package com.example.raporkayit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.UUID;

@Getter
@AllArgsConstructor

public class VergiKoduResponse {
    private final UUID vergiKoduId;
    private final String vergiKodu;
    private final String vergiKoduAdi;
}
