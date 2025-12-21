package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record CreateTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    Instant occurredAt,
    UUID categoryId,
    BigDecimal amount,
    Optional<String> description,
    TransactionType type,
    Optional<UUID> transferId) {
  public CreateTransactionCommand(
      UUID organizationId,
      UUID userId,
      UUID accountId,
      Instant occurredAt,
      UUID categoryId,
      BigDecimal amount,
      TransactionType type,
      Optional<UUID> transferId) {
    this(
        organizationId,
        userId,
        accountId,
        occurredAt,
        categoryId,
        amount,
        Optional.empty(),
        type,
        transferId);
  }

  public CreateTransactionCommand(
      UUID organizationId,
      UUID userId,
      UUID accountId,
      Instant occurredAt,
      UUID categoryId,
      BigDecimal amount,
      String description,
      TransactionType type,
      Optional<UUID> transferId) {
    this(
        organizationId,
        userId,
        accountId,
        occurredAt,
        categoryId,
        amount,
        Optional.of(description),
        type,
        transferId);
  }
}
