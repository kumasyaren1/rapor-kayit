package com.example.raporkayit.repository;

import com.example.raporkayit.entity.VergiKodu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VergiKoduRepository
        extends JpaRepository<VergiKodu, UUID>{
    List<VergiKodu> findAllByAktifTrueOrderByVergiKoduAdiAsc();
}
