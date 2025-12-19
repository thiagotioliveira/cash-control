package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.domain.event.account.v1.CreditApplied;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.DebitApplied;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ConfirmTransactionCommand(
    UUID organizationId,
    UUID userId,
    UUID accountId,
    UUID transactionId,
    Instant occurredAt,
    BigDecimal balanceAfter,
    BigDecimal amount) {

  public ConfirmTransactionCommand(DebitApplied event) {
    this(
        event.getOrganizationId(),
        event.getUserId(),
        event.getAccountId(),
        event.getTransactionId(),
        event.occurredAt(),
        event.getBalanceAfter(),
        event.getAmount());
  }

  public ConfirmTransactionCommand(CreditApplied event) {
    this(
        event.getOrganizationId(),
        event.getUserId(),
        event.getAccountId(),
        event.getTransactionId(),
        event.occurredAt(),
        event.getBalanceAfter(),
        event.getAmount());
  }
}
