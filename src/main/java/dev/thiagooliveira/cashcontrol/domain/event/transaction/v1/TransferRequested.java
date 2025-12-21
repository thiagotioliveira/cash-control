package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferRequested(
    UUID id,
    UUID organizationId,
    UUID userId,
    UUID accountIdTo,
    UUID accountIdFrom,
    UUID categoryIdTo,
    UUID categoryIdFrom,
    String description,
    BigDecimal amountFrom,
    BigDecimal amountTo,
    Instant occurredAt,
    int version)
    implements DomainEvent {
  @Override
  public UUID aggregateId() {
    return id;
  }
}
