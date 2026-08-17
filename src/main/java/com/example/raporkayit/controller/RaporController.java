package com.example.raporkayit.controller;

import com.example.raporkayit.dto.*;
import com.example.raporkayit.service.RaporService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/raporlar")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class RaporController {

    private final RaporService raporService;

    @PostMapping
    public ResponseEntity<RaporResponse> olustur(@RequestBody @Valid RaporOlusturRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raporService.olustur(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RaporResponse> getirById(@PathVariable String id) {
        return ResponseEntity.ok(raporService.getirById(id));
    }

    @GetMapping
    public ResponseEntity<Page<RaporResponse>> sorgula(
            @ModelAttribute RaporSorguCriteria criteria,
            @PageableDefault(sort = "duzenlemeTarihi", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(raporService.sorgula(criteria, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RaporResponse> guncelle(
            @PathVariable String id,
            @RequestBody @Valid RaporOlusturRequest request) {
        return ResponseEntity.ok(raporService.guncelle(id, request));
    }

    @PutMapping("/{id}/iptal")
    public ResponseEntity<RaporResponse> iptalEt(@PathVariable String id) {
        return ResponseEntity.ok(raporService.iptalEt(id));
    }

    @PostMapping("/{id}/tahakkuk")
    public ResponseEntity<RaporResponse> tahakkukKes(@PathVariable String id) {
        return ResponseEntity.ok(raporService.tahakkukKes(id));
    }

    @PostMapping("/{id}/cevap")
    public ResponseEntity<RaporResponse> cevapKaydet(
            @PathVariable String id,
            @RequestBody @Valid CevapKayitRequest request) {
        return ResponseEntity.ok(raporService.cevapKaydet(id, request));
    }
}