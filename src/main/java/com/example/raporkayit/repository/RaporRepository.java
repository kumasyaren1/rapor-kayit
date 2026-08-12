package com.example.raporkayit.repository;

import com.example.raporkayit.entity.Rapor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RaporRepository
        extends JpaRepository<Rapor, UUID> {

    boolean existsByRaporKayitNo(String raporKayitNo);
}