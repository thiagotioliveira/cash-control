package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.shared.Page;
import dev.thiagooliveira.cashcontrol.shared.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

  Optional<GetTransactionItem> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  Page<GetTransactionItem> findAllByOrganizationIdAndAccountIdAndDueDateBetween(
      UUID organizationId,
      UUID accountId,
      LocalDate startDate,
      LocalDate endDate,
      Pageable pageable);

  List<GetTransactionItem> findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);
}
