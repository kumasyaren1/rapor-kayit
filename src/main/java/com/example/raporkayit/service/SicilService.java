package com.example.raporkayit.service;

import com.example.raporkayit.repository.MukellefRepository;
import org.springframework.stereotype.Service;
import com.example.raporkayit.dto.MukellefResponse;
import com.example.raporkayit.entity.Mukellef;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.util.StringUtils;

@Service
public class SicilService {
    private final MukellefRepository mukellefRepository;

    public SicilService(MukellefRepository mukellefRepository) {
        this.mukellefRepository = mukellefRepository;
    }
    public MukellefResponse mukellefSorgula(  //dto dönüş tipli metot
            String vergiKimlikNo,
            String tcKimlikNo) { // 2 adet parametremiz var.

        boolean vknVar = StringUtils.hasText(vergiKimlikNo); // içlerinde text var mı diye bakar " " kabul etmez.
        boolean tcknVar = StringUtils.hasText(tcKimlikNo);

        if (!vknVar && !tcknVar) { // ikisi de aynı anda yoksa hata verir.
            throw new IllegalArgumentException( // throw normal akışı durdurup hata fırlatır
                    "Vergi Kimlik No veya T.C. Kimlik No girilmelidir."
            );
        }

        Mukellef mukellef; // mükellef türünde değişken tanımladık

        if (vknVar && tcknVar) {
            mukellef = mukellefRepository
                    .findByVergiKimlikNoAndTcKimlikNoAndAktifTrue(
                            vergiKimlikNo,
                            tcKimlikNo
                    )
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Girilen VKN ve TCKN ile eşleşen aktif mükellef bulunamadı."
                    ));

        } else if (vknVar) {
            mukellef = mukellefRepository
                    .findByVergiKimlikNoAndAktifTrue(vergiKimlikNo)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Girilen Vergi Kimlik No ile aktif mükellef bulunamadı."
                    ));

        } else {
            mukellef = mukellefRepository
                    .findByTcKimlikNoAndAktifTrue(tcKimlikNo)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Girilen T.C. Kimlik No ile aktif mükellef bulunamadı."
                    ));
        }

        return new MukellefResponse( //dto
                mukellef.getVergiKimlikNo(),
                mukellef.getTcKimlikNo(),
                mukellef.getAdSoyadUnvan()
        );
    }
}
