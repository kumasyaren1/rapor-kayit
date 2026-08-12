package com.example.raporkayit.controller;

import com.example.raporkayit.dto.RaporOlusturRequest;
import com.example.raporkayit.dto.RaporResponse;
import com.example.raporkayit.service.RaporService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/raporlar")
public class RaporController {

    private final RaporService raporService;

    public RaporController(RaporService raporService) {
        this.raporService = raporService;
    }

    @PostMapping
    public ResponseEntity<RaporResponse> raporOlustur(@Valid @RequestBody RaporOlusturRequest request) {
        RaporResponse response = raporService.raporOlustur(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}