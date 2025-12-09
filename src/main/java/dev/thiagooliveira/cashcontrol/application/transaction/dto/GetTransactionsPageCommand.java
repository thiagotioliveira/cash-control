package dev.thiagooliveira.cashcontrol.application.transaction.dto;

import dev.thiagooliveira.cashcontrol.shared.Pageable;
import java.time.LocalDate;
import java.util.UUID;

public record GetTransactionsPageCommand(
    UUID organizationId,
    UUID accountId,
    LocalDate startDate,
    LocalDate endDate,
    Pageable pageable) {

  public GetTransactionsPageCommand(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate) {
    this(organizationId, accountId, startDate, endDate, new Pageable(0, 10));
  }
}
