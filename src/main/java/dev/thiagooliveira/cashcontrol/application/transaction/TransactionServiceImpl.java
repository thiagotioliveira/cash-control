package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService {

  private final GetTransactions getTransactions;

  public TransactionServiceImpl(GetTransactions getTransactions) {
    this.getTransactions = getTransactions;
  }

  @Override
  public boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id) {
    return this.getTransactions.isLatestTransaction(organizationId, accountId, id);
  }

  @Override
  public List<TransactionSummary> get(GetTransactionsCommand command) {
    return this.getTransactions.execute(command);
  }

  @Override
  public Optional<TransactionSummary> get(GetTransactionCommand command) {
    return this.getTransactions.execute(command);
  }
}
