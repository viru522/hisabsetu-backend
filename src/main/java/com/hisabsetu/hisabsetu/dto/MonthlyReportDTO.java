package com.hisabsetu.hisabsetu.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyReportDTO {

    private String month;
    private Double income;
    private Double expense;

    // 🔥 Custom constructor for raw query mapping
    public MonthlyReportDTO(String month, Number income, Number expense) {
        this.month = month;
        this.income = income != null ? income.doubleValue() : 0.0;
        this.expense = expense != null ? expense.doubleValue() : 0.0;
    }
}