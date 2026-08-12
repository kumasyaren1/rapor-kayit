package com.example.raporkayit.repository;

import com.example.raporkayit.entity.Mukellef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MukellefRepository
     extends JpaRepository<Mukellef, UUID> {
    Optional<Mukellef> findByVergiKimlikNoAndAktifTrue(
            String vergiKimlikNo
    );

    Optional<Mukellef> findByTcKimlikNoAndAktifTrue(
            String tcKimlikNo
    );
    Optional<Mukellef> findByVergiKimlikNoAndTcKimlikNoAndAktifTrue(
            String vergiKimlikNo,
            String tcKimlikNo
    );
}
