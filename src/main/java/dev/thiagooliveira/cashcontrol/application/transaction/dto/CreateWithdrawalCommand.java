package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record CreateWithdrawalCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    Instant occurredAt,
    UUID categoryId,
    BigDecimal amount,
    Optional<String> description) {
  public CreateWithdrawalCommand(
      UUID organizationId,
      UUID userId,
      UUID accountId,
      Instant occurredAt,
      UUID categoryId,
      BigDecimal amount) {
    this(organizationId, userId, accountId, occurredAt, categoryId, amount, Optional.empty());
  }

  public CreateWithdrawalCommand(
      UUID organizationId,
      UUID userId,
      UUID accountId,
      Instant occurredAt,
      UUID categoryId,
      BigDecimal amount,
      String description) {
    this(
        organizationId,
        userId,
        accountId,
        occurredAt,
        categoryId,
        amount,
        Optional.of(description));
  }
}
