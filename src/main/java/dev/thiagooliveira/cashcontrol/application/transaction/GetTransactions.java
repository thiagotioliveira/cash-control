package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsPageCommand;
import dev.thiagooliveira.cashcontrol.shared.Page;
import java.util.List;

public class GetTransactions {

  private final TransactionRepository repository;

  public GetTransactions(TransactionRepository repository) {
    this.repository = repository;
  }

  public Page<GetTransactionItem> execute(GetTransactionsPageCommand command) {
    return this.repository.findAllByOrganizationIdAndAccountIdAndDueDateBetween(
        command.organizationId(),
        command.accountId(),
        command.startDate(),
        command.endDate(),
        command.pageable());
  }

  public List<GetTransactionItem> execute(GetTransactionsCommand command) {
    return this.repository.findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
        command.organizationId(), command.accountId(), command.startDate(), command.endDate());
  }
}
