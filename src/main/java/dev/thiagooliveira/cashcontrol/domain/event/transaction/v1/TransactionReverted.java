package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record TransactionReverted(
    UUID organizationId,
    UUID accountId,
    UUID transactionId,
    UUID userId,
    Instant occurredAt,
    int version)
    implements DomainEvent {
  @Override
  public UUID aggregateId() {
    return transactionId;
  }
}
