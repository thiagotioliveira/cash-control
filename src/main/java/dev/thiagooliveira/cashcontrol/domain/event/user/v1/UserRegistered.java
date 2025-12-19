package dev.thiagooliveira.cashcontrol.domain.event.user.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record UserRegistered(UUID userId, UUID organizationId, Instant occurredAt, int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return userId;
  }
}
