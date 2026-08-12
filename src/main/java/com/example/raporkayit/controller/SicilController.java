package com.example.raporkayit.controller;

import com.example.raporkayit.dto.MukellefResponse; //Yalnızca görünmesini istediğimiz bilgileri taşıyan DTO’yu veriyor.
import com.example.raporkayit.service.SicilService; // Mükellef sorgulama iş kurallarını çalıştıran servis
import org.springframework.web.bind.annotation.*; //Spring’in web anotasyonlarını kullanabilmemizi sağlar.

@CrossOrigin(origins = "http://localhost:4200")
@RestController //Bu sınıf HTTP isteklerini karşılayan bir REST controller’dır. Metotların döndürdüğü nesneleri JSON’a çevir
@RequestMapping("/api/sicil") //Bu controller içerisindeki bütün endpointlerin ortak adresini belirler.

public class SicilController {

    private final SicilService sicilService;//Bu controller çalışabilmek için bir SicilService nesnesine ihtiyaç duyuyor ve onu bu değişkende tutacak

    public SicilController(SicilService sicilService) {
        this.sicilService = sicilService;//Constructor’a gelen SicilService nesnesini, controller’ın içerisindeki sicilService değişkeninde sakla.
    }

    @GetMapping("/mukellef") //Bu metodu bir HTTP GET isteğine bağlar.
    public MukellefResponse mukellefSorgula(
            @RequestParam(required = false) String vergiKimlikNo,//@RequestParam, URL’de ? işaretinden sonra gönderilen değerleri alır.
            @RequestParam(required = false) String tcKimlikNo) { // required = false Bu parametrenin tek başına zorunlu olmadığını belirtir

        return sicilService.mukellefSorgula( //Burada controller, aldığı değerleri service’e gönderiyor.
                vergiKimlikNo,
                tcKimlikNo
        );
    }
}