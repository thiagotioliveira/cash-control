package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record GetTransactionItem(
    UUID transactionId,
    UUID accountId,
    String accountName,
    Optional<UUID> userId,
    Optional<String> userName,
    Currency currency,
    Optional<UUID> transactionTemplateId,
    Optional<Instant> occurredAt,
    LocalDate dueDate,
    String description,
    BigDecimal amount,
    Optional<BigDecimal> accountBalance,
    UUID categoryId,
    String categoryName,
    String categoryHashColor,
    TransactionType type,
    TransactionStatus status,
    Optional<Recurrence> recurrence,
    Optional<Integer> installments) {}
