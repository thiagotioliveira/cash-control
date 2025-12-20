package dev.thiagooliveira.cashcontrol.application.account.dto;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.ScheduledTransactionRequested;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionRequested;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ApplyCreditCommand(
    UUID organizationId,
    UUID accountId,
    UUID transactionId,
    UUID userId,
    BigDecimal amount,
    Instant occurredAt) {

  public ApplyCreditCommand(TransactionRequested event) {
    this(
        event.organizationId(),
        event.accountId(),
        event.transactionId(),
        event.userId(),
        event.amount(),
        event.occurredAt());
  }

  public ApplyCreditCommand(ScheduledTransactionRequested event) {
    this(
        event.organizationId(),
        event.accountId(),
        event.transactionId(),
        event.userId(),
        event.amount(),
        event.occurredAt());
  }
}
