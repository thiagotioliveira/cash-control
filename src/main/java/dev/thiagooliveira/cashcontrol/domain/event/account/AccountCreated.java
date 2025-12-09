package dev.thiagooliveira.cashcontrol.domain.event.account;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountCreated(
    UUID accountId, String name, UUID bankId, BigDecimal balance, Instant occurredAt, int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return accountId;
  }
}
