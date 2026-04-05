package com.hisabsetu.hisabsetu.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate transactionDate;

    @NotBlank
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}