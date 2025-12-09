package dev.thiagooliveira.cashcontrol.application.account.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record CreateTransactionCommand(
    UUID accountId,
    Instant occurredAt,
    UUID categoryId,
    BigDecimal amount,
    Optional<String> description) {
  public CreateTransactionCommand(
      UUID accountId, Instant occurredAt, UUID categoryId, BigDecimal amount) {
    this(accountId, occurredAt, categoryId, amount, Optional.empty());
  }

  public CreateTransactionCommand(
      UUID accountId, Instant occurredAt, UUID categoryId, BigDecimal amount, String description) {
    this(accountId, occurredAt, categoryId, amount, Optional.of(description));
  }
}
