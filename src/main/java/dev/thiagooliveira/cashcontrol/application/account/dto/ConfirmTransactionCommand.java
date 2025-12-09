package dev.thiagooliveira.cashcontrol.application.account.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ConfirmTransactionCommand(
    UUID accountId, UUID transactionId, Instant occurredAt, BigDecimal amount) {}
