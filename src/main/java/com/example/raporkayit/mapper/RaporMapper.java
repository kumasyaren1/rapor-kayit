package com.example.raporkayit.mapper;

import com.example.raporkayit.dto.RaporResponse;
import com.example.raporkayit.entity.*;
import org.springframework.stereotype.Component;

@Component
public class RaporMapper {

    public RaporResponse toResponse(Rapor r) {
        return toResponse(r, null, null);
    }

    public RaporResponse toResponse(Rapor r, Cevap cevap, Tahakkuk tahakkuk) {
        RaporTuru rt = r.getRaporTuru();
        AnaRaporTuru art = rt.getAnaRaporTuru();
        VergiKodu vk = r.getVergiKodu();

        return new RaporResponse(
                r.getRaporId(), r.getRaporKayitNo(),
                r.getVergiKimlikNo(), r.getTcKimlikNo(), r.getAdSoyadUnvan(),
                r.getDuzenlemeTarihi(), r.getAciklama(), r.getDurum(),
                art.getAnaRaporTuruId(), art.getAnaRaporTuruKodu(), art.getAnaRaporTuruAdi(),
                rt.getRaporTuruId(), rt.getRaporTuruKodu(), rt.getRaporTuruAdi(),
                vk.getVergiKoduId(), vk.getVergiKodu(), vk.getVergiKoduAdi(),
                cevap != null ? cevap.getCevapNumarasi() : null,
                cevap != null ? cevap.getCevapTarihi() : null,
                cevap != null ? cevap.getSonuc() : null,
                tahakkuk != null ? tahakkuk.getTahakkukFisNo() : null,
                tahakkuk != null ? tahakkuk.getTahakkukTarihi() : null
        );
    }
}