package com.example.raporkayit.service;

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

// @RequiredArgsConstructor — Lombok'a "tüm 'final' alanları içeren bir constructor üret" diyoruz.
// Bu, RaporController'da elle yazdığımız constructor'ın BİREBİR AYNISINI otomatik üretiyor
// (dependency injection mantığı hiç değişmedi, sadece 7 satırlık constructor kodunu yazmaktan kurtulduk).
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
                .durum("KAYITLI")
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
        return raporMapper.toResponse(rapor);
    }

    // ============================================================
    // UC-01: Sorgulama / Listeleme
    // ============================================================
    @Transactional(readOnly = true)
    public Page<RaporResponse> sorgula(RaporSorguCriteria criteria, Pageable pageable) {

        // Specification: veritabanına gidecek WHERE koşulunu, hangi filtreler
        // doluysa ona göre PARÇA PARÇA inşa etmemizi sağlayan bir JPA aracı.
        // Kullanıcı sadece "durum" filtrelediyse, sadece "WHERE durum = ..." üretilir;
        // hem "durum" hem "vergiKimlikNo" doldurduysa, ikisi "AND" ile birleşir.
        // Mantığı Repository'deki "isimden otomatik SQL üretimi" ile aynı aileden,
        // sadece burada koşullar SABİT değil, İSTEĞE göre DEĞİŞKEN.
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
            if (StringUtils.hasText(criteria.getDurum())) {
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

        // findAll(spec, pageable): Specification'daki koşullarla FİLTRELE,
        // pageable'daki sayfa/boyut/sıralama bilgisiyle SAYFALA.
        // .map(raporMapper::toResponse): dönen Page<Rapor> (Entity), Page<RaporResponse) (DTO)'ya çevriliyor
        // — burada da Mapper'ı tekrar kullanıyoruz, ayrı bir çeviri kodu yazmadık.
        return raporRepository.findAll(spec, pageable).map(raporMapper::toResponse);
    }

    // ============================================================
    // UC-04: Güncelle
    // ============================================================
    @Transactional
    public RaporResponse guncelle(UUID id, RaporOlusturRequest request) {
        Rapor rapor = bul(id);

        if (!"KAYITLI".equals(rapor.getDurum())) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlar güncellenebilir.");
        }

        RaporTuru raporTuru = raporTuruRepository.findById(request.getRaporTuruId())
                .orElseThrow(() -> new EntityNotFoundException("Rapor türü bulunamadı."));

        if (!raporTuru.getAnaRaporTuru().getAnaRaporTuruId().equals(request.getAnaRaporTuruId())) {
            throw new IllegalArgumentException("Seçilen rapor türü, seçilen ana rapor türüne ait değildir.");
        }

        VergiKodu vergiKodu = vergiKoduRepository.findById(request.getVergiKoduId())
                .orElseThrow(() -> new EntityNotFoundException("Vergi kodu bulunamadı."));

        // Dikkat: burada Builder KULLANMIYORUZ. Builder, YENİ bir nesne kurarken işe yarar.
        // Burada elimizde zaten veritabanından çekilmiş, Hibernate'in "gözetimindeki" (managed)
        // bir Rapor var — sadece alanlarını değiştiriyoruz, Hibernate bu değişikliği kendisi
        // fark edip (dirty checking) metot bitince otomatik UPDATE atacak. Bu yüzden
        // raporRepository.save(...) çağırmaya bile GEREK YOK, ama okunabilirlik için
        // yine de açıkça çağırıyoruz.
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

        if (!"KAYITLI".equals(rapor.getDurum())) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlar iptal edilebilir.");
        }

        rapor.setDurum("IPTAL");
        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    // ============================================================
    // UC-05: Cevap Kayıt
    // Burada GERÇEKTEN 2 ayrı veritabanı işlemi var (Cevap kaydet + Rapor durumunu güncelle).
    // @Transactional konuşmamızdaki tam senaryo bu — ikisi de ya birlikte başarılı olmalı,
    // ya da (bir hata çıkarsa) ikisi de geri alınmalı. Metodun üstündeki @Transactional
    // bunu garanti ediyor.
    // ============================================================
    @Transactional
    public RaporResponse cevapKaydet(UUID id, CevapKayitRequest request) {
        Rapor rapor = bul(id);

        if (!"KAYITLI".equals(rapor.getDurum())) {
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
        cevapRepository.save(cevap);   // 1. veritabanı işlemi

        rapor.setDurum("CEVAPLANDI");  // 2. işlem — dirty checking ile otomatik UPDATE

        return raporMapper.toResponse(rapor);
    }

    // ============================================================
    // UC-07: Tahakkuk Kes
    // ============================================================
    @Transactional
    public RaporResponse tahakkukKes(UUID id) {
        Rapor rapor = bul(id);

        if ("IPTAL".equals(rapor.getDurum()) || "TAHAKKUK_KESILDI".equals(rapor.getDurum())) {
            throw new IllegalStateException("İptal edilmiş veya tahakkuku zaten kesilmiş rapora işlem yapılamaz.");
        }

        Tahakkuk tahakkuk = Tahakkuk.builder()
                .tahakkukFisNo(tahakkukFisNoUret())
                .tahakkukTarihi(LocalDate.now())
                .rapor(rapor)
                .build();
        tahakkukRepository.save(tahakkuk);

        rapor.setDurum("TAHAKKUK_KESILDI");

        return raporMapper.toResponse(rapor);
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