package dev.thiagooliveira.cashcontrol.application.account.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GetAccountItem(
    UUID id, String name, UUID bankId, Instant updatedAt, BigDecimal balance) {}
