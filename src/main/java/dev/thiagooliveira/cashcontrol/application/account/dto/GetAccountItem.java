package dev.thiagooliveira.cashcontrol.application.account.dto;

import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record GetAccountItem(
    UUID id, String name, UUID bankId, Currency currency, Instant updatedAt, BigDecimal balance) {}
