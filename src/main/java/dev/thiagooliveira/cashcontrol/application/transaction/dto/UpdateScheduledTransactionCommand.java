package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionTemplateUpdated;
import java.math.BigDecimal;
import java.util.UUID;

public record UpdateScheduledTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID templateId,
    String description,
    BigDecimal amount,
    int dueDay) {

  public UpdateScheduledTransactionCommand(TransactionTemplateUpdated event) {
    this(
        event.organizationId(),
        event.userId(),
        event.accountId(),
        event.templateId(),
        event.description(),
        event.amount(),
        event.dueDate().getDayOfMonth());
  }
}
