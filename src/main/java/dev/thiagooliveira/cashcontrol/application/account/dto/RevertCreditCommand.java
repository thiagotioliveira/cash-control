package dev.thiagooliveira.cashcontrol.application.account.dto;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.RevertTransactionRequested;
import java.math.BigDecimal;
import java.util.UUID;

public record RevertCreditCommand(
    UUID organizationId, UUID accountId, UUID transactionId, UUID userId, BigDecimal amount) {

  public RevertCreditCommand(RevertTransactionRequested event) {
    this(
        event.getOrganizationId(),
        event.getAccountId(),
        event.getTransactionId(),
        event.getUserId(),
        event.getAmount());
  }
}
