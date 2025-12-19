package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ReceivableCreated(
    UUID templateId,
    UUID organizationId,
    UUID accountId,
    UUID userId,
    UUID categoryId,
    BigDecimal amount,
    String description,
    LocalDate startDueDate,
    Recurrence recurrence,
    Integer installments,
    Instant occurredAt,
    int version)
    implements DomainEvent {
  @Override
  public UUID aggregateId() {
    return templateId;
  }
}
