package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {

  boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id);

  List<TransactionSummary> get(GetTransactionsCommand command);

  Optional<TransactionSummary> get(GetTransactionCommand command);
}
