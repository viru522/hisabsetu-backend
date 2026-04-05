package com.hisabsetu.hisabsetu.repository;

import com.hisabsetu.hisabsetu.entity.Transaction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends
        JpaRepository<Transaction, Long>,
        JpaSpecificationExecutor<Transaction> {

    // 🔥 SUMMARY (WORKS)
    @Query("""
    SELECT 
        COALESCE(SUM(CASE WHEN t.type = 'IN' THEN t.amount ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN t.type = 'OUT' THEN t.amount ELSE 0 END), 0)
    FROM Transaction t
    """)
    List<Object[]> getSummaryRaw();


    // 🔥 MONTHLY REPORT (FIXED FOR POSTGRESQL)
    @Query("""
    SELECT 
        EXTRACT(YEAR FROM t.transactionDate),
        EXTRACT(MONTH FROM t.transactionDate),
        COALESCE(SUM(CASE WHEN t.type = 'IN' THEN t.amount ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN t.type = 'OUT' THEN t.amount ELSE 0 END), 0)
    FROM Transaction t
    GROUP BY 
        EXTRACT(YEAR FROM t.transactionDate),
        EXTRACT(MONTH FROM t.transactionDate)
    ORDER BY 
        EXTRACT(YEAR FROM t.transactionDate),
        EXTRACT(MONTH FROM t.transactionDate)
    """)
    List<Object[]> getMonthlyReportRaw();


    // 🔥 OPTIONAL TYPE FILTER
    Page<Transaction> findByType(String type, Pageable pageable);


    // 🔥 DATE RANGE FILTER (FIXED PARAMS)
    @Query("""
    SELECT t FROM Transaction t
    WHERE (:start IS NULL OR t.transactionDate >= :start)
      AND (:end IS NULL OR t.transactionDate <= :end)
    """)
    Page<Transaction> findByDateRange(
            LocalDate start,
            LocalDate end,
            Pageable pageable
    );


    // 🔥 USER FILTER
    List<Transaction> findByUserUsername(String username);
}