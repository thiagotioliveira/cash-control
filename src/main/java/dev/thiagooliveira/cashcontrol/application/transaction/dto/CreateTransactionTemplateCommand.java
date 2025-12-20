package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record CreateTransactionTemplateCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID categoryId,
    Optional<String> description,
    BigDecimal amount,
    LocalDate startDueDate,
    Recurrence recurrence,
    Optional<Integer> installments,
    TransactionType type) {}
