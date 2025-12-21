package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record TransferConfirmed(UUID id, Instant occurredAt, int version) implements DomainEvent {
  @Override
  public UUID aggregateId() {
    return id;
  }
}
