package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import java.util.List;

public class GetTransactions {

  private final TransactionRepository repository;

  public GetTransactions(TransactionRepository repository) {
    this.repository = repository;
  }

  public List<GetTransactionItem> execute(GetTransactionsCommand command) {
    return this.repository.findAllByAccountIdAndDueDateBetween(
        command.accountId(), command.startDate(), command.endDate());
  }
}
