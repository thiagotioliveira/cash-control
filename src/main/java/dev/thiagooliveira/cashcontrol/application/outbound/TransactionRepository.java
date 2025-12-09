package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

  Optional<GetTransactionItem> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  List<GetTransactionItem> findAllByOrganizationIdAndAccountIdAndDueDateBetween(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);
}
