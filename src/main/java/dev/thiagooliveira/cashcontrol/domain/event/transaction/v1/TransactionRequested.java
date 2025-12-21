package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public record TransactionRequested(
    UUID transactionId,
    UUID accountId,
    UUID organizationId,
    UUID userId,
    UUID categoryId,
    String description,
    BigDecimal amount,
    Instant occurredAt,
    TransactionType type,
    Optional<UUID> transferId,
    int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return transactionId;
  }
}
