package dev.thiagooliveira.cashcontrol.application.transfer.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateTransferCommand(
    UUID organizationId,
    UUID userId,
    UUID accountIdFrom,
    UUID accountIdTo,
    UUID categoryId,
    String description,
    Instant occurredAt,
    BigDecimal amountFrom,
    BigDecimal amountTo) {}
