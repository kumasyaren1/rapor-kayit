package com.example.raporkayit.repository;

import com.example.raporkayit.entity.Rapor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RaporRepository extends JpaRepository<Rapor, UUID>, JpaSpecificationExecutor<Rapor> {
    Optional<Rapor> findByRaporKayitNo(String raporKayitNo);
    boolean existsByRaporKayitNo(String raporKayitNo);
}