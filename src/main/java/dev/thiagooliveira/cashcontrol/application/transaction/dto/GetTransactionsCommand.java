package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record GetTransactionsCommand(
    UUID organizationId, Optional<UUID> accountId, LocalDate startDate, LocalDate endDate) {

  public GetTransactionsCommand(UUID organizationId, LocalDate startDate, LocalDate endDate) {
    this(organizationId, Optional.empty(), startDate, endDate);
  }

  public GetTransactionsCommand(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate) {
    this(organizationId, Optional.of(accountId), startDate, endDate);
  }
}
