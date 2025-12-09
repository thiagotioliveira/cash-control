package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

  Optional<GetTransactionItem> findByIdAndAccountId(UUID id, UUID accountId);

  List<GetTransactionItem> findAllByAccountIdAndDueDateBetween(
      UUID accountId, LocalDate startDate, LocalDate endDate);
}
