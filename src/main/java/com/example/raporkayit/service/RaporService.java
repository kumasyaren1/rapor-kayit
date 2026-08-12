package com.example.raporkayit.service;

import com.example.raporkayit.dto.MukellefResponse;
import com.example.raporkayit.dto.RaporOlusturRequest;
import com.example.raporkayit.dto.RaporResponse;
import com.example.raporkayit.entity.AnaRaporTuru;
import com.example.raporkayit.entity.Rapor;
import com.example.raporkayit.entity.RaporTuru;
import com.example.raporkayit.entity.VergiKodu;
import com.example.raporkayit.repository.RaporRepository;
import com.example.raporkayit.repository.RaporTuruRepository;
import com.example.raporkayit.repository.VergiKoduRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class RaporService {

    private final RaporRepository raporRepository;
    private final RaporTuruRepository raporTuruRepository;
    private final VergiKoduRepository vergiKoduRepository;
    private final SicilService sicilService;

    public RaporService(RaporRepository raporRepository,
                        RaporTuruRepository raporTuruRepository,
                        VergiKoduRepository vergiKoduRepository,
                        SicilService sicilService) {
        this.raporRepository = raporRepository;
        this.raporTuruRepository = raporTuruRepository;
        this.vergiKoduRepository = vergiKoduRepository;
        this.sicilService = sicilService;
    }

    public RaporResponse raporOlustur(RaporOlusturRequest request) {

        // 1) Mükellef doğrulaması — SicilService'e devrediyoruz
        MukellefResponse mukellef = sicilService.mukellefSorgula(
                request.getVergiKimlikNo(),
                request.getTcKimlikNo()
        );

        // 2) Tarih kontrolü
        if (request.getDuzenlemeTarihi().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Düzenleme tarihi bugünden ileri olamaz.");
        }

        // 3) Referans verilerini doğrula
        RaporTuru raporTuru = raporTuruRepository.findById(request.getRaporTuruId())
                .orElseThrow(() -> new EntityNotFoundException("Rapor türü bulunamadı."));

        if (!raporTuru.getAnaRaporTuru()
                .getAnaRaporTuruId()
                .equals(request.getAnaRaporTuruId())) {

            throw new IllegalArgumentException(
                    "Seçilen rapor türü, seçilen ana rapor türüne ait değildir."
            );
        }
        
        VergiKodu vergiKodu = vergiKoduRepository.findById(request.getVergiKoduId())
                .orElseThrow(() -> new EntityNotFoundException("Vergi kodu bulunamadı."));

        // 4) Yeni Entity'yi kur
        Rapor rapor = new Rapor();
        rapor.setRaporKayitNo(raporKayitNoUret());
        rapor.setVergiKimlikNo(request.getVergiKimlikNo());
        rapor.setTcKimlikNo(request.getTcKimlikNo());
        rapor.setAdSoyadUnvan(mukellef.getAdSoyadUnvan());
        rapor.setDuzenlemeTarihi(request.getDuzenlemeTarihi());
        rapor.setAciklama(request.getAciklama());
        rapor.setDurum("KAYITLI");
        rapor.setRaporTuru(raporTuru);
        rapor.setVergiKodu(vergiKodu);

        // 5) Veritabanına yaz
        Rapor kaydedilen = raporRepository.save(rapor);

        // 6) DTO'ya çevirip dön
        return toResponse(kaydedilen);
    }

    private String raporKayitNoUret() {
        String kod;
        do {
            int yil = LocalDate.now().getYear();
            int rastgele = (int) (Math.random() * 900000) + 100000;
            kod = "RPR-" + yil + "-" + rastgele;
        } while (raporRepository.existsByRaporKayitNo(kod));
        return kod;
    }

    private RaporResponse toResponse(Rapor r) {
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