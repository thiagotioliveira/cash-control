package dev.thiagooliveira.cashcontrol.domain.event.user;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.time.Instant;
import java.util.UUID;

public record UserInvited(
    UUID userId, String name, String email, UUID organizationId, Instant occurredAt, int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return userId;
  }
}
