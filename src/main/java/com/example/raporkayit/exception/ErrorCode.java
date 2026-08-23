package com.example.raporkayit.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    RAPOR_NOT_FOUND(HttpStatus.NOT_FOUND),
    RAPOR_TURU_NOT_FOUND(HttpStatus.NOT_FOUND),
    VERGI_KODU_NOT_FOUND(HttpStatus.NOT_FOUND),
    RAPOR_TURU_UYUMSUZ(HttpStatus.BAD_REQUEST),
    DUZENLEME_TARIHI_ILERI(HttpStatus.BAD_REQUEST),
    CEVAP_TARIHI_RAPOR_TARIHINDEN_ONCE(HttpStatus.BAD_REQUEST),
    CEVAP_TARIHI_ILERI(HttpStatus.BAD_REQUEST),
    RAPOR_SADECE_KAYITLI_GUNCELLENEBILIR(HttpStatus.CONFLICT),
    RAPOR_SADECE_KAYITLI_IPTAL_EDILEBILIR(HttpStatus.CONFLICT),
    RAPOR_SADECE_KAYITLI_CEVAPLANABILIR(HttpStatus.CONFLICT),
    RAPOR_TAHAKKUK_KESILEMEZ_DURUM(HttpStatus.CONFLICT);

    private final HttpStatus httpStatus;

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}