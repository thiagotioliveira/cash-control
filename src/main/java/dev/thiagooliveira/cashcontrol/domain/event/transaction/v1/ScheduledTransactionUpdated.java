package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduledTransactionUpdated(
    UUID transactionId,
    UUID accountId,
    BigDecimal amount,
    String description,
    LocalDate dueDate,
    UUID organizationId,
    UUID userId,
    Instant occurredAt,
    int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return accountId;
  }
}
