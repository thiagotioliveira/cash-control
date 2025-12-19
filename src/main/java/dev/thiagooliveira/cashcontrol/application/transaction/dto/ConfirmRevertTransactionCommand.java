package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.domain.event.account.v1.CreditReverted;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.DebitReverted;
import java.util.UUID;

public record ConfirmRevertTransactionCommand(
    UUID organizationId, UUID userId, UUID accountId, UUID transactionId) {

  public ConfirmRevertTransactionCommand(CreditReverted event) {
    this(
        event.getOrganizationId(),
        event.getUserId(),
        event.getAccountId(),
        event.getTransactionId());
  }

  public ConfirmRevertTransactionCommand(DebitReverted event) {
    this(
        event.getOrganizationId(),
        event.getUserId(),
        event.getAccountId(),
        event.getTransactionId());
  }
}
