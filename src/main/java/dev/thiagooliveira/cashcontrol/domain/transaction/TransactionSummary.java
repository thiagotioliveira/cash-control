package dev.thiagooliveira.cashcontrol.domain.transaction;

import dev.thiagooliveira.cashcontrol.shared.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record TransactionSummary(
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
    CategoryType categoryType,
    TransactionType type,
    TransactionStatus status,
    Optional<Recurrence> recurrence,
    Optional<Integer> installments) {}
