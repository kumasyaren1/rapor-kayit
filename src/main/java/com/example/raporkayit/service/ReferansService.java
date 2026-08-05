package com.example.raporkayit.service;

import com.example.raporkayit.dto.AnaRaporTuruResponse;
import com.example.raporkayit.dto.RaporTuruResponse;
import com.example.raporkayit.dto.VergiKoduResponse;
import com.example.raporkayit.entity.AnaRaporTuru;
import com.example.raporkayit.entity.RaporTuru;
import com.example.raporkayit.entity.VergiKodu;
import com.example.raporkayit.repository.AnaRaporTuruRepository;
import com.example.raporkayit.repository.RaporTuruRepository;
import com.example.raporkayit.repository.VergiKoduRepository;
import java.util.UUID;


import org.springframework.stereotype.Service;
import com.example.raporkayit.dto.RaporTuruResponse;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReferansService {
    private final AnaRaporTuruRepository anaRaporTuruRepository; // Ana rapor türü verilerine erişmek için kullanılan repository bağımlılığı
    private final RaporTuruRepository raporTuruRepository;
    private final VergiKoduRepository vergiKoduRepository;


    public ReferansService(
            AnaRaporTuruRepository anaRaporTuruRepository,
            RaporTuruRepository raporTuruRepository,
            VergiKoduRepository vergiKoduRepository) {

        this.anaRaporTuruRepository = anaRaporTuruRepository;
        this.raporTuruRepository = raporTuruRepository;
        this.vergiKoduRepository = vergiKoduRepository;

        //constructor sayesinde spring repo nesnesini getirip ekliyor,bu değişkenle db ye sorgu atabiliriz.
    }

    public List<AnaRaporTuruResponse> aktifAnaRaporTurleriniGetir() { //liste şeklinde döndüren metodumuz

        List<AnaRaporTuru> anaRaporTurleri =
                anaRaporTuruRepository
                        .findAllByAktifTrueOrderByAnaRaporTuruAdiAsc(); //db ye gidip aktif tüm ana rapor türlerini alfabetik getir dedik.

        List<AnaRaporTuruResponse> responseListesi =
                new ArrayList<>(); // db den gelen entityleri dtolara çevirmek için boş liste açtık

        for (AnaRaporTuru anaRaporTuru : anaRaporTurleri) { // tüm gelen entitye bakar dış dünyaya çıkacak olan bilgileri çekip response nesnesi oluşturur.

            AnaRaporTuruResponse response =
                    new AnaRaporTuruResponse(
                            anaRaporTuru.getAnaRaporTuruId(),
                            anaRaporTuru.getAnaRaporTuruKodu(),
                            anaRaporTuru.getAnaRaporTuruAdi()
                    );

            responseListesi.add(response);
        }
        return responseListesi;// controller a geri fırlattık .
    }

    public List<RaporTuruResponse> aktifRaporTurleriniGetir(
            UUID anaRaporTuruId) {

        List<RaporTuru> raporTurleri =
                raporTuruRepository
                        .findAllByAnaRaporTuru_AnaRaporTuruIdAndAktifTrueOrderByRaporTuruAdiAsc(anaRaporTuruId);

        List<RaporTuruResponse> responseListesi =
                new ArrayList<>();

        for (RaporTuru raporTuru : raporTurleri) {

            RaporTuruResponse response =
                    new RaporTuruResponse(
                            raporTuru.getRaporTuruId(),
                            raporTuru.getRaporTuruKodu(),
                            raporTuru.getRaporTuruAdi()
                    );

            responseListesi.add(response);
        }
        return responseListesi;
    }

    public List<VergiKoduResponse> aktifVergiKodlariniGetir() {
        List<VergiKodu> vergiKodlari =
                vergiKoduRepository
                        .findAllByAktifTrueOrderByVergiKoduAdiAsc();

        List<VergiKoduResponse> responseListesi =
                new ArrayList<>();
        for (VergiKodu vergiKodu : vergiKodlari) {

            VergiKoduResponse response =
                    new VergiKoduResponse(
                            vergiKodu.getVergiKoduId(),
                            vergiKodu.getVergiKodu(),
                            vergiKodu.getVergiKoduAdi()
                    );
            responseListesi.add(response);
        }
        return responseListesi;
    }
}
