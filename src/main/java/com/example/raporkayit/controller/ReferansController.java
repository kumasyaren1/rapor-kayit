package com.example.raporkayit.controller;

import com.example.raporkayit.dto.AnaRaporTuruResponse;
import com.example.raporkayit.dto.RaporTuruResponse;
import com.example.raporkayit.dto.VergiKoduResponse;
import com.example.raporkayit.service.ReferansService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/referanslar") // ortak adres
public class ReferansController {
    private final ReferansService referansService;

    public ReferansController(ReferansService referansService) {
        this.referansService = referansService;
    }
    @GetMapping("/ana-rapor-turleri")
    public List<AnaRaporTuruResponse> aktifAnaRaporTurleriniGetir() {
        return referansService.aktifAnaRaporTurleriniGetir();

    }
    @GetMapping("/ana-rapor-turleri/{anaRaporTuruId}/rapor-turleri")
    public List<RaporTuruResponse> aktifRaporTurleriniGetir(
            @PathVariable UUID anaRaporTuruId) {

        return referansService.aktifRaporTurleriniGetir(anaRaporTuruId);
    }
    @GetMapping("/vergi-kodlari")
    public List<VergiKoduResponse> aktifVergiKodlariniGetir() {
        return referansService.aktifVergiKodlariniGetir();
    }
}
