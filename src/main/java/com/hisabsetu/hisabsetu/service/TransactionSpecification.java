package com.hisabsetu.hisabsetu.service;

import com.hisabsetu.hisabsetu.entity.Transaction;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TransactionSpecification {

    public static Specification<Transaction> filter(
            String keyword,
            String type,
            LocalDate start,
            LocalDate end,
            Double minAmount,
            Double maxAmount
    ) {
        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            // 🔥 KEYWORD
            if (keyword != null && !keyword.isEmpty()) {
                var like = "%" + keyword.toLowerCase() + "%";

                predicates = cb.and(predicates,
                        cb.or(
                                cb.like(cb.lower(root.get("category")), like),
                                cb.like(cb.lower(root.get("paymentMethod")), like)
                        )
                );
            }

            // 🔥 TYPE
            if (type != null && !type.isEmpty()) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("type"), type));
            }

            // 🔥 DATE
            if (start != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("transactionDate"), start));
            }

            if (end != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("transactionDate"), end));
            }

            // 🔥 AMOUNT
            if (minAmount != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }

            if (maxAmount != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            return predicates;
        };
    }
}