package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ConfirmScheduledTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID transactionId,
    Instant occurredAt,
    BigDecimal amount) {}
