package com.example.raporkayit.service;

import com.example.raporkayit.Enum.RaporDurumu;
import com.example.raporkayit.dto.*;
import com.example.raporkayit.entity.*;
import com.example.raporkayit.mapper.RaporMapper;
import com.example.raporkayit.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RaporService {

    private final RaporRepository raporRepository;
    private final RaporTuruRepository raporTuruRepository;
    private final VergiKoduRepository vergiKoduRepository;
    private final CevapRepository cevapRepository;
    private final TahakkukRepository tahakkukRepository;
    private final SicilService sicilService;
    private final RaporMapper raporMapper;

    // ============================================================
    // UC-02: Rapor Oluştur
    // ============================================================
    @Transactional
    public RaporResponse olustur(RaporOlusturRequest request) {

        MukellefResponse mukellef = sicilService.mukellefSorgula(
                request.getVergiKimlikNo(),
                request.getTcKimlikNo()
        );

        if (request.getDuzenlemeTarihi().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Düzenleme tarihi bugünden ileri olamaz.");
        }

        RaporTuru raporTuru = raporTuruRepository.findById(request.getRaporTuruId())
                .orElseThrow(() -> new EntityNotFoundException("Rapor türü bulunamadı."));

        if (!raporTuru.getAnaRaporTuru().getAnaRaporTuruId().equals(request.getAnaRaporTuruId())) {
            throw new IllegalArgumentException("Seçilen rapor türü, seçilen ana rapor türüne ait değildir.");
        }

        VergiKodu vergiKodu = vergiKoduRepository.findById(request.getVergiKoduId())
                .orElseThrow(() -> new EntityNotFoundException("Vergi kodu bulunamadı."));

        Rapor rapor = Rapor.builder()
                .raporKayitNo(raporKayitNoUret())
                .vergiKimlikNo(mukellef.getVergiKimlikNo())
                .tcKimlikNo(mukellef.getTcKimlikNo())
                .adSoyadUnvan(mukellef.getAdSoyadUnvan())
                .duzenlemeTarihi(request.getDuzenlemeTarihi())
                .aciklama(request.getAciklama())
                .durum(RaporDurumu.KAYITLI)
                .raporTuru(raporTuru)
                .vergiKodu(vergiKodu)
                .build();

        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    // ============================================================
    // UC-03: Görüntüle
    // ============================================================
    @Transactional(readOnly = true)
    public RaporResponse getirById(UUID id) {
        Rapor rapor = bul(id);
        Cevap cevap = cevapRepository.findByRapor_RaporId(id).orElse(null);
        Tahakkuk tahakkuk = tahakkukRepository.findByRapor_RaporId(id).orElse(null);
        return raporMapper.toResponse(rapor, cevap, tahakkuk);
    }

    // ============================================================
    // UC-01: Sorgulama / Listeleme
    // ============================================================
    @Transactional(readOnly = true)
    public Page<RaporResponse> sorgula(RaporSorguCriteria criteria, Pageable pageable) {

        Specification<Rapor> spec = (root, query, cb) -> {
            List<Predicate> kosullar = new ArrayList<>();

            if (StringUtils.hasText(criteria.getRaporKayitNo())) {
                kosullar.add(cb.equal(root.get("raporKayitNo"), criteria.getRaporKayitNo().trim()));
            }
            if (StringUtils.hasText(criteria.getVergiKimlikNo())) {
                kosullar.add(cb.equal(root.get("vergiKimlikNo"), criteria.getVergiKimlikNo().trim()));
            }
            if (StringUtils.hasText(criteria.getTcKimlikNo())) {
                kosullar.add(cb.equal(root.get("tcKimlikNo"), criteria.getTcKimlikNo().trim()));
            }
            // durum artık RaporDurumu (enum) — null kontrolü yeterli, hasText metin için gereksiz.
            if (criteria.getDurum() != null) {
                kosullar.add(cb.equal(root.get("durum"), criteria.getDurum()));
            }
            if (criteria.getRaporTuruId() != null) {
                kosullar.add(cb.equal(root.get("raporTuru").get("raporTuruId"), criteria.getRaporTuruId()));
            }
            if (criteria.getAnaRaporTuruId() != null) {
                kosullar.add(cb.equal(
                        root.get("raporTuru").get("anaRaporTuru").get("anaRaporTuruId"),
                        criteria.getAnaRaporTuruId()));
            }
            if (criteria.getBaslangicTarihi() != null) {
                kosullar.add(cb.greaterThanOrEqualTo(root.get("duzenlemeTarihi"), criteria.getBaslangicTarihi()));
            }
            if (criteria.getBitisTarihi() != null) {
                kosullar.add(cb.lessThanOrEqualTo(root.get("duzenlemeTarihi"), criteria.getBitisTarihi()));
            }

            return cb.and(kosullar.toArray(new Predicate[0]));
        };

        return raporRepository.findAll(spec, pageable).map(raporMapper::toResponse);
    }

    // ============================================================
    // UC-04: Güncelle
    // ============================================================
    @Transactional
    public RaporResponse guncelle(UUID id, RaporOlusturRequest request) {
        Rapor rapor = bul(id);

        if (rapor.getDurum() != RaporDurumu.KAYITLI) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlar güncellenebilir.");
        }
        if (request.getDuzenlemeTarihi().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Düzenleme tarihi bugünden ileri olamaz."
            );
        }

        RaporTuru raporTuru = raporTuruRepository.findById(request.getRaporTuruId())
                .orElseThrow(() -> new EntityNotFoundException("Rapor türü bulunamadı."));

        if (!raporTuru.getAnaRaporTuru().getAnaRaporTuruId().equals(request.getAnaRaporTuruId())) {
            throw new IllegalArgumentException("Seçilen rapor türü, seçilen ana rapor türüne ait değildir.");
        }

        VergiKodu vergiKodu = vergiKoduRepository.findById(request.getVergiKoduId())
                .orElseThrow(() -> new EntityNotFoundException("Vergi kodu bulunamadı."));

        rapor.setRaporTuru(raporTuru);
        rapor.setVergiKodu(vergiKodu);
        rapor.setDuzenlemeTarihi(request.getDuzenlemeTarihi());
        rapor.setAciklama(request.getAciklama());

        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    // ============================================================
    // UC-06: İptal Et
    // ============================================================
    @Transactional
    public RaporResponse iptalEt(UUID id) {
        Rapor rapor = bul(id);

        if (rapor.getDurum() != RaporDurumu.KAYITLI) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlar iptal edilebilir.");
        }

        rapor.setDurum(RaporDurumu.IPTAL);
        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    // ============================================================
    // UC-05: Cevap Kayıt
    // ============================================================
    @Transactional
    public RaporResponse cevapKaydet(UUID id, CevapKayitRequest request) {
        Rapor rapor = bul(id);

        if (rapor.getDurum() != RaporDurumu.KAYITLI) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlara cevap eklenebilir.");
        }

        if (request.getCevapTarihi().isBefore(rapor.getDuzenlemeTarihi())) {
            throw new IllegalArgumentException("Cevap tarihi, rapor tarihinden önce olamaz.");
        }
        if (request.getCevapTarihi().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Cevap tarihi bugünden ileri olamaz.");
        }

        Cevap cevap = Cevap.builder()
                .cevapNumarasi(request.getCevapNumarasi())
                .cevapTarihi(request.getCevapTarihi())
                .sonuc(request.getCevapSonucu())
                .rapor(rapor)
                .build();
        cevapRepository.save(cevap);

        rapor.setDurum(RaporDurumu.CEVAPLANDI);

        return raporMapper.toResponse(rapor, cevap, null);
    }

    // ============================================================
    // UC-07: Tahakkuk Kes
    // ============================================================
    @Transactional
    public RaporResponse tahakkukKes(UUID id) {
        Rapor rapor = bul(id);

        if (rapor.getDurum() != RaporDurumu.KAYITLI && rapor.getDurum() != RaporDurumu.CEVAPLANDI) {
            throw new IllegalStateException(
                    "Sadece KAYITLI ve CEVAPLANDI durumundaki raporlara tahakkuk kesilebilir."
            );
        }
        Tahakkuk tahakkuk = Tahakkuk.builder()
                .tahakkukFisNo(tahakkukFisNoUret())
                .tahakkukTarihi(LocalDate.now())
                .rapor(rapor)
                .build();
        tahakkukRepository.save(tahakkuk);

        rapor.setDurum(RaporDurumu.TAHAKKUK_KESILDI);

        return raporMapper.toResponse(rapor, null, tahakkuk);
    }

    // ============================================================
    // Yardımcı (private) metotlar
    // ============================================================
    private Rapor bul(UUID id) {
        return raporRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rapor bulunamadı: " + id));
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

    private String tahakkukFisNoUret() {
        int yil = LocalDate.now().getYear();
        int rastgele = (int) (Math.random() * 900000) + 100000;
        return "TAH-" + yil + "-" + rastgele;
    }
}