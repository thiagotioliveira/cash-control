package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduledTransactionCreated(
    UUID transactionId,
    UUID accountId,
    UUID organizationId,
    UUID templateId,
    UUID categoryId,
    TransactionType type,
    String description,
    BigDecimal amount,
    LocalDate dueDate,
    Instant occurredAt,
    int version)
    implements DomainEvent {
  @Override
  public UUID aggregateId() {
    return transactionId;
  }
}
