package com.hisabsetu.hisabsetu.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionDTO {

    @NotNull
    private LocalDate transactionDate;

    @NotBlank
    @Pattern(regexp = "IN|OUT", message = "Type must be IN or OUT")
    private String type;

    @NotNull
    @Positive
    private Double amount;

    @NotBlank
    private String category;

    @NotBlank
    private String paymentMethod;

    private String partyType;
    private String partyName;
    private String note;
}