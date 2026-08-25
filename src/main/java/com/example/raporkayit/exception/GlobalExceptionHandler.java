package com.example.raporkayit.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    // 1. Bizim fırlattığımız tüm iş kuralı (business) hatalarını yakalar
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex) {
        String message = messageSource.getMessage(
                ex.getErrorCode().name(),
                ex.getArgs(),
                Locale.forLanguageTag("tr"));

        return buildResponse(ex.getErrorCode().getHttpStatus(), ex.getErrorCode().name(), message);
    }

    // 2. Query parametresine geçersiz enum girildiğinde Spring'in fırlattığı hatayı yakalar
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "GECERSIZ_PARAMETRE",
                "Geçersiz parametre değeri: " + ex.getValue());
    }

    // 3. DTO'lardaki @NotBlank, @NotNull gibi Bean Validation ihlallerini yakalar
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDASYON_HATASI", message);
    }

    // Ortak JSON yanıt gövdesi oluşturan yardımcı metot
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        if (code != null) {
            body.put("code", code);
        }
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}