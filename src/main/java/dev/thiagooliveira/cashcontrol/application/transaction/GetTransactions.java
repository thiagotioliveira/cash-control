package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;

public class GetTransactions {

  private final TransactionRepository repository;

  public GetTransactions(TransactionRepository repository) {
    this.repository = repository;
  }

  public List<TransactionSummary> execute(GetTransactionsCommand command) {
    return this.repository.findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
        command.organizationId(), command.accountId(), command.startDate(), command.endDate());
  }

  public Optional<TransactionSummary> execute(GetTransactionCommand command) {
    return this.repository.findByOrganizationIdAndAccountIdAndId(
        command.organizationId(), command.accountId(), command.transactionId());
  }
}
