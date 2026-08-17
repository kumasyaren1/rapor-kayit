package com.example.raporkayit.service;

import com.example.raporkayit.dto.*;
import com.example.raporkayit.entity.Rapor;
import com.example.raporkayit.entity.RaporTuru;
import com.example.raporkayit.entity.VergiKodu;
import com.example.raporkayit.mapper.RaporMapper;
import com.example.raporkayit.repository.RaporRepository;
import com.example.raporkayit.repository.RaporTuruRepository;
import com.example.raporkayit.repository.VergiKoduRepository;
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
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RaporService {

    private final RaporRepository raporRepository;
    private final RaporTuruRepository raporTuruRepository;
    private final VergiKoduRepository vergiKoduRepository;
    private final SicilService sicilService;
    private final RaporMapper raporMapper;

    @Transactional
    public RaporResponse olustur(RaporOlusturRequest request) {
        MukellefResponse mukellef = sicilService.mukellefSorgula(
                request.getVergiKimlikNo(),
                request.getTcKimlikNo()
        );

        RaporTuru raporTuru = raporTuruRepository.findById(request.getRaporTuruId())
                .orElseThrow(() -> new NoSuchElementException("Rapor türü bulunamadı"));

        VergiKodu vergiKodu = vergiKoduRepository.findById(request.getVergiKoduId())
                .orElseThrow(() -> new NoSuchElementException("Vergi kodu bulunamadı"));

        Rapor rapor = new Rapor();
        rapor.setRaporKayitNo(raporKayitNoUret());
        rapor.setVergiKimlikNo(mukellef.getVergiKimlikNo());
        rapor.setTcKimlikNo(mukellef.getTcKimlikNo());
        rapor.setAdSoyadUnvan(mukellef.getAdSoyadUnvan());
        rapor.setRaporTuru(raporTuru);
        rapor.setVergiKodu(vergiKodu);
        rapor.setDuzenlemeTarihi(request.getDuzenlemeTarihi());
        rapor.setAciklama(request.getAciklama());
        rapor.setDurum("KAYITLI");

        Rapor kaydedilen = raporRepository.save(rapor);
        return raporMapper.toResponse(kaydedilen);
    }

    @Transactional(readOnly = true)
    public RaporResponse getirById(String id) {
        UUID uuid = UUID.fromString(id);
        Rapor rapor = raporRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Rapor bulunamadı: " + id));
        return raporMapper.toResponse(rapor);
    }

    @Transactional
    public RaporResponse guncelle(String id, RaporOlusturRequest request) {
        UUID uuid = UUID.fromString(id);
        Rapor rapor = raporRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Rapor bulunamadı: " + id));

        if (!"KAYITLI".equals(rapor.getDurum())) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlar güncellenebilir.");
        }

        RaporTuru raporTuru = raporTuruRepository.findById(request.getRaporTuruId())
                .orElseThrow(() -> new NoSuchElementException("Rapor türü bulunamadı"));

        VergiKodu vergiKodu = vergiKoduRepository.findById(request.getVergiKoduId())
                .orElseThrow(() -> new NoSuchElementException("Vergi kodu bulunamadı"));

        rapor.setRaporTuru(raporTuru);
        rapor.setVergiKodu(vergiKodu);
        rapor.setDuzenlemeTarihi(request.getDuzenlemeTarihi());
        rapor.setAciklama(request.getAciklama());

        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    @Transactional
    public RaporResponse iptalEt(String id) {
        UUID uuid = UUID.fromString(id);
        Rapor rapor = raporRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Rapor bulunamadı: " + id));

        if (!"KAYITLI".equals(rapor.getDurum())) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlar iptal edilebilir.");
        }

        rapor.setDurum("IPTAL");
        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    @Transactional
    public RaporResponse tahakkukKes(String id) {
        UUID uuid = UUID.fromString(id);
        Rapor rapor = raporRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Rapor bulunamadı: " + id));

        if ("IPTAL".equals(rapor.getDurum()) || "TAHAKKUK_KESILDI".equals(rapor.getDurum())) {
            throw new IllegalStateException("İptal edilmiş veya zaten tahakkuku kesilmiş rapora işlem yapılamaz.");
        }

        rapor.setDurum("TAHAKKUK_KESILDI");
        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    @Transactional
    public RaporResponse cevapKaydet(String id, CevapKayitRequest request) {
        UUID uuid = UUID.fromString(id);
        Rapor rapor = raporRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("Rapor bulunamadı: " + id));

        if (!"KAYITLI".equals(rapor.getDurum())) {
            throw new IllegalStateException("Sadece KAYITLI durumundaki raporlara cevap eklenebilir.");
        }

        rapor.setDurum("CEVAPLANDI");
        return raporMapper.toResponse(raporRepository.save(rapor));
    }

    @Transactional(readOnly = true)
    public Page<RaporResponse> sorgula(RaporSorguCriteria criteria, Pageable pageable) {
        Specification<Rapor> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(criteria.getRaporKayitNo())) {
                predicates.add(cb.equal(root.get("raporKayitNo"), criteria.getRaporKayitNo().trim()));
            }
            if (StringUtils.hasText(criteria.getVergiKimlikNo())) {
                predicates.add(cb.equal(root.get("vergiKimlikNo"), criteria.getVergiKimlikNo().trim()));
            }
            if (StringUtils.hasText(criteria.getTcKimlikNo())) {
                predicates.add(cb.equal(root.get("tcKimlikNo"), criteria.getTcKimlikNo().trim()));
            }
            if (StringUtils.hasText(criteria.getDurum())) {
                predicates.add(cb.equal(root.get("durum"), criteria.getDurum()));
            }
            if (criteria.getAnaRaporTuruId() != null) {
                predicates.add(cb.equal(root.get("raporTuru").get("anaRaporTuru").get("id"), criteria.getAnaRaporTuruId()));
            }
            if (criteria.getRaporTuruId() != null) {
                predicates.add(cb.equal(root.get("raporTuru").get("id"), criteria.getRaporTuruId()));
            }
            if (criteria.getBaslangicTarihi() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("duzenlemeTarihi"), criteria.getBaslangicTarihi()));
            }
            if (criteria.getBitisTarihi() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("duzenlemeTarihi"), criteria.getBitisTarihi()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return raporRepository.findAll(spec, pageable).map(raporMapper::toResponse);
    }

    private String raporKayitNoUret() {
        int yil = LocalDate.now().getYear();
        long sira = raporRepository.count() + 1;
        return String.format("RPR-%d-%04d", yil, sira);
    }
}