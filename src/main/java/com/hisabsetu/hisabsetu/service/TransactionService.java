package com.hisabsetu.hisabsetu.service;

import com.hisabsetu.hisabsetu.dto.MonthlyReportDTO;
import com.hisabsetu.hisabsetu.dto.PageResponseDTO;
import com.hisabsetu.hisabsetu.dto.TransactionDTO;
import com.hisabsetu.hisabsetu.entity.Transaction;
import com.hisabsetu.hisabsetu.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository repo;

    // ✅ CREATE
    public Transaction save(TransactionDTO dto) {
        return repo.save(convertToEntity(dto));
    }

    // ✅ GET ALL
    public PageResponseDTO<Transaction> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("transactionDate").descending()
        );

        Page<Transaction> result = repo.findAll(pageable);

        return new PageResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    // ✅ GET BY ID
    public Transaction getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
    }

    // ✅ SUMMARY (FULL SAFE)
    public Map<String, Double> getSummary() {

        List<Object[]> resultList = repo.getSummaryRaw();

        if (resultList == null || resultList.isEmpty()) {
            return Map.of(
                    "totalIn", 0.0,
                    "totalOut", 0.0,
                    "balance", 0.0
            );
        }

        Object[] row = resultList.get(0);

        if (row == null || row.length < 2) {
            return Map.of(
                    "totalIn", 0.0,
                    "totalOut", 0.0,
                    "balance", 0.0
            );
        }

        double totalIn = row[0] != null ? ((Number) row[0]).doubleValue() : 0;
        double totalOut = row[1] != null ? ((Number) row[1]).doubleValue() : 0;

        return Map.of(
                "totalIn", totalIn,
                "totalOut", totalOut,
                "balance", totalIn - totalOut
        );
    }

    // ✅ MONTHLY REPORT (FULL SAFE)
    public List<MonthlyReportDTO> getMonthlyReport() {

        List<Object[]> rows = repo.getMonthlyReportRaw();

        if (rows == null || rows.isEmpty()) {
            return Collections.emptyList();
        }

        List<MonthlyReportDTO> result = new ArrayList<>();

        for (Object[] row : rows) {

            if (row == null || row.length < 4) continue;

            int year = row[0] != null ? ((Number) row[0]).intValue() : 0;
            int month = row[1] != null ? ((Number) row[1]).intValue() : 0;

            if (year == 0 || month == 0) continue;

            String formattedMonth = String.format("%d-%02d", year, month);

            double income = row[2] != null ? ((Number) row[2]).doubleValue() : 0;
            double expense = row[3] != null ? ((Number) row[3]).doubleValue() : 0;

            result.add(new MonthlyReportDTO(formattedMonth, income, expense));
        }

        return result;
    }

    // ✅ FILTER (RETURN DTO, NOT PAGE)
    public PageResponseDTO<Transaction> filter(
            String keyword,
            String type,
            LocalDate start,
            LocalDate end,
            Double minAmount,
            Double maxAmount,
            int page,
            int size
    ) {

        Specification<Transaction> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 🔥 KEYWORD
            if (keyword != null && !keyword.isBlank()) {
                String k = "%" + keyword.toLowerCase() + "%";

                predicates.add(
                        cb.or(
                                cb.like(cb.lower(root.get("category")), k),
                                cb.like(cb.lower(root.get("note")), k),
                                cb.like(cb.lower(root.get("partyName")), k)
                        )
                );
            }

            // 🔥 TYPE
            if (type != null && !type.isBlank() && !type.equalsIgnoreCase("ALL")) {
                predicates.add(cb.equal(root.get("type"), type));
            }

            // 🔥 DATE
            if (start != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), start));
            }

            if (end != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), end));
            }

            // 🔥 AMOUNT (NEW)
            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("transactionDate").descending()
        );

        Page<Transaction> result = repo.findAll(spec, pageable);

        return new PageResponseDTO<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    // ✅ UPDATE
    public Transaction update(Long id, TransactionDTO dto) {

        Transaction t = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        t.setTransactionDate(dto.getTransactionDate());
        t.setType(dto.getType());
        t.setAmount(dto.getAmount());
        t.setCategory(dto.getCategory());
        t.setPaymentMethod(dto.getPaymentMethod());
        t.setPartyType(dto.getPartyType());
        t.setPartyName(dto.getPartyName());
        t.setNote(dto.getNote());

        return repo.save(t);
    }

    // ✅ DELETE
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Transaction not found");
        }
        repo.deleteById(id);
    }

    private Transaction convertToEntity(TransactionDTO dto) {

        Transaction t = new Transaction();

        t.setTransactionDate(dto.getTransactionDate());
        t.setType(dto.getType());
        t.setAmount(dto.getAmount());
        t.setCategory(dto.getCategory());
        t.setPaymentMethod(dto.getPaymentMethod());
        t.setPartyType(dto.getPartyType());
        t.setPartyName(dto.getPartyName());
        t.setNote(dto.getNote());

        return t;
    }
}