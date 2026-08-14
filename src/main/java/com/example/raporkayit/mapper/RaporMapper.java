package com.example.raporkayit.mapper;

import com.example.raporkayit.dto.RaporResponse;
import com.example.raporkayit.entity.AnaRaporTuru;
import com.example.raporkayit.entity.Rapor;
import com.example.raporkayit.entity.RaporTuru;
import com.example.raporkayit.entity.VergiKodu;
import org.springframework.stereotype.Component;

@Component
public class RaporMapper {

    public RaporResponse toResponse(Rapor r) {
        RaporTuru rt = r.getRaporTuru();
        AnaRaporTuru art = rt.getAnaRaporTuru();
        VergiKodu vk = r.getVergiKodu();

        return new RaporResponse(
                r.getRaporId(), r.getRaporKayitNo(),
                r.getVergiKimlikNo(), r.getTcKimlikNo(), r.getAdSoyadUnvan(),
                r.getDuzenlemeTarihi(), r.getAciklama(), r.getDurum(),
                art.getAnaRaporTuruId(), art.getAnaRaporTuruKodu(), art.getAnaRaporTuruAdi(),
                rt.getRaporTuruId(), rt.getRaporTuruKodu(), rt.getRaporTuruAdi(),
                vk.getVergiKoduId(), vk.getVergiKodu(), vk.getVergiKoduAdi()
        );
    }
}