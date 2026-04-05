package com.hisabsetu.hisabsetu.controller;

import com.hisabsetu.hisabsetu.dto.MonthlyReportDTO;
import com.hisabsetu.hisabsetu.dto.PageResponseDTO;
import com.hisabsetu.hisabsetu.dto.TransactionDTO;
import com.hisabsetu.hisabsetu.entity.Transaction;
import com.hisabsetu.hisabsetu.service.TransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    // ✅ CREATE
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping
    public Transaction create(@Valid @RequestBody TransactionDTO dto) {
        return service.save(dto);
    }

    // ✅ GET ALL (ONLY ONE METHOD)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping
    public PageResponseDTO<Transaction> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.getAll(page, size);
    }

    // ✅ GET BY ID
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public Transaction getById(@PathVariable Long id) {
        return service.getById(id);
    }

    // ✅ SUMMARY
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/summary")
    public Map<String, Double> getSummary() {
        return service.getSummary();
    }

    // ✅ MONTHLY REPORT
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/reports/monthly")
    public List<MonthlyReportDTO> getMonthlyReport() {
        return service.getMonthlyReport();
    }

    // ✅ UPDATE
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/{id}")
    public Transaction update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO dto
    ) {
        return service.update(id, dto);
    }

    // ✅ DELETE
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Map<String, String> delete(@PathVariable Long id) {

        service.delete(id);

        return Map.of(
                "message", "Deleted successfully",
                "id", String.valueOf(id)
        );
    }

    // ✅ FILTER
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/filter")
    public PageResponseDTO<Transaction> filter(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate start,

            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate end,

            @RequestParam(required = false) Double minAmount,   // 🔥 NEW
            @RequestParam(required = false) Double maxAmount,   // 🔥 NEW

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.filter(keyword, type, start, end, minAmount, maxAmount, page, size);
    }
}