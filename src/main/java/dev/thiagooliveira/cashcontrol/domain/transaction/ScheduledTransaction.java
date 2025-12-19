package dev.thiagooliveira.cashcontrol.domain.transaction;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduledTransaction(
    UUID id,
    UUID organizationId,
    UUID accountId,
    UUID templateId,
    UUID categoryId,
    LocalDate dueDate,
    String description,
    BigDecimal amount,
    TransactionType type) {}
