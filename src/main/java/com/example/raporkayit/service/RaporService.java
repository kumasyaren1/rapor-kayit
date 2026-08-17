package com.example.raporkayit.service;

import com.example.raporkayit.dto.MukellefResponse;
import com.example.raporkayit.dto.RaporOlusturRequest;
import com.example.raporkayit.dto.RaporResponse;
import com.example.raporkayit.entity.Rapor;
import com.example.raporkayit.entity.RaporTuru;
import com.example.raporkayit.entity.VergiKodu;
import com.example.raporkayit.mapper.RaporMapper;
import com.example.raporkayit.repository.RaporRepository;
import com.example.raporkayit.repository.RaporTuruRepository;
import com.example.raporkayit.repository.VergiKoduRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class RaporService {

    private final RaporRepository raporRepository;
    private final RaporTuruRepository raporTuruRepository;
    private final VergiKoduRepository vergiKoduRepository;
    private final SicilService sicilService;
    private final RaporMapper raporMapper;   // YENİ

    public RaporService(RaporRepository raporRepository,
                        RaporTuruRepository raporTuruRepository,
                        VergiKoduRepository vergiKoduRepository,
                        SicilService sicilService,
                        RaporMapper raporMapper) {   // YENİ parametre
        this.raporRepository = raporRepository;
        this.raporTuruRepository = raporTuruRepository;
        this.vergiKoduRepository = vergiKoduRepository;
        this.sicilService = sicilService;
        this.raporMapper = raporMapper;   // YENİ
    }

    public RaporResponse raporOlustur(RaporOlusturRequest request) {

        // 1) Mükellef doğrulaması
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
        Rapor rapor = Rapor.builder()
                .raporKayitNo(raporKayitNoUret())
                .vergiKimlikNo(request.getVergiKimlikNo())
                .tcKimlikNo(request.getTcKimlikNo())
                .adSoyadUnvan(mukellef.getAdSoyadUnvan())
                .duzenlemeTarihi(request.getDuzenlemeTarihi())
                .aciklama(request.getAciklama())
                .durum("KAYITLI")
                .raporTuru(raporTuru)
                .vergiKodu(vergiKodu)
                .build();
        // 5) Veritabanına yaz
        Rapor kaydedilen = raporRepository.save(rapor);

        // 6) DTO'ya çevirip dön — artık Mapper'a devrediyoruz
        return raporMapper.toResponse(kaydedilen);
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
    public RaporResponse raporGetir(UUID id) {
        Rapor rapor = raporRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapor bulunamadı."));

        return raporMapper.toResponse(rapor);
    }
}