package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.time.Instant;
import java.util.UUID;

public record CategoryCreated(
    UUID categoryId,
    String name,
    String hashColor,
    TransactionType type,
    UUID organizationId,
    Instant occurredAt,
    int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return categoryId;
  }
}
