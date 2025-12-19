package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record UpdateScheduledTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID transactionId,
    String description,
    BigDecimal amount,
    LocalDate dueDate,
    Optional<LocalDate> endDueDate) {}
