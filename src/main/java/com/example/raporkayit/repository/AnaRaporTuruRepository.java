package com.example.raporkayit.repository;

import com.example.raporkayit.entity.AnaRaporTuru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface AnaRaporTuruRepository
    extends JpaRepository<AnaRaporTuru, UUID>{  //Ben sıfırdan bir depo yazmıyorum; Spring'in hazine dairesinden miras alıyorum entity, tipi
    List<AnaRaporTuru> findAllByAktifTrueOrderByAnaRaporTuruAdiAsc();
}
