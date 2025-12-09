package dev.thiagooliveira.cashcontrol.domain.event.account;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ScheduledTransactionCreated(
    UUID transactionId,
    UUID accountId,
    UUID categoryId,
    TransactionType type,
    BigDecimal amount,
    String description,
    LocalDate dueDate,
    Recurrence recurrence,
    Integer totalInstallments,
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
