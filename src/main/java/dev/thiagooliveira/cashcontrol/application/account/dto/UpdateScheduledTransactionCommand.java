package dev.thiagooliveira.cashcontrol.application.account.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record UpdateScheduledTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID transactionId,
    BigDecimal amount,
    int dueDayOfMonth,
    Optional<LocalDate> endDueDate) {}
