package com.example.raporkayit.repository;

import com.example.raporkayit.entity.Tahakkuk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TahakkukRepository
        extends JpaRepository<Tahakkuk, UUID> {}