package com.hisabsetu.hisabsetu.config.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔥 HANDLE RUNTIME (BUSINESS ERRORS)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        return ResponseEntity.badRequest().body(
                Map.of("error", ex.getMessage())
        );
    }

    // 🔥 HANDLE ALL OTHER ERRORS
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        ex.printStackTrace(); // 🔥 important for debugging

        return ResponseEntity.status(500).body(
                Map.of("error", "Internal Server Error")
        );
    }
}