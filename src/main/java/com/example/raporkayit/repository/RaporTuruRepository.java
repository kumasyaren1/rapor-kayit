package com.example.raporkayit.repository;

import com.example.raporkayit.entity.RaporTuru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RaporTuruRepository
        extends JpaRepository<RaporTuru, UUID> {

    List<RaporTuru>
    findAllByAnaRaporTuru_AnaRaporTuruIdAndAktifTrueOrderByRaporTuruAdiAsc(
            UUID anaRaporTuruId
    );
}

// Önceki repository metodu tüm aktif ana rapor türlerini getiriyordu.
// Bu sorgu metodu, seçilen ana rapor türünün UUID değerini alır
// ve yalnızca ona bağlı aktif rapor türlerini getirir.