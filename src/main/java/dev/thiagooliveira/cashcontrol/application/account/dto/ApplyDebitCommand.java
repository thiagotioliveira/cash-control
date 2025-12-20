package dev.thiagooliveira.cashcontrol.application.account.dto;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.ScheduledTransactionRequested;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionRequested;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ApplyDebitCommand(
    UUID organizationId,
    UUID accountId,
    UUID transactionId,
    UUID userId,
    BigDecimal amount,
    Instant occurredAt) {

  public ApplyDebitCommand(TransactionRequested event) {
    this(
        event.organizationId(),
        event.accountId(),
        event.transactionId(),
        event.userId(),
        event.amount(),
        event.occurredAt());
  }

  public ApplyDebitCommand(ScheduledTransactionRequested event) {
    this(
        event.organizationId(),
        event.accountId(),
        event.transactionId(),
        event.userId(),
        event.amount(),
        event.occurredAt());
  }
}
