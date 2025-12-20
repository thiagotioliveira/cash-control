package dev.thiagooliveira.cashcontrol.infrastructure.transactional.transaction;

import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.*;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionServiceProxy implements TransactionService {

  private final TransactionService transactionService;

  public TransactionServiceProxy(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @Override
  public boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id) {
    return this.transactionService.isLatestTransaction(organizationId, accountId, id);
  }

  @Override
  public List<TransactionSummary> get(GetTransactionsCommand command) {
    return this.transactionService.get(command);
  }

  @Override
  public Optional<TransactionSummary> get(GetTransactionCommand command) {
    return this.transactionService.get(command);
  }

  @Override
  public void create(CreateTransactionCommand command) {
    this.transactionService.create(command);
  }

  @Override
  public void confirm(ConfirmTransactionCommand command) {
    this.transactionService.confirm(command);
  }

  @Override
  public void confirm(ConfirmScheduledTransactionCommand command) {
    this.transactionService.confirm(command);
  }

  @Override
  public void confirm(ConfirmRevertTransactionCommand command) {
    this.transactionService.confirm(command);
  }

  @Override
  public void update(UpdateTransactionTemplateCommand command) {
    this.transactionService.update(command);
  }

  @Override
  public void update(UpdateScheduledTransactionCommand command) {
    this.transactionService.update(command);
  }

  @Override
  public void createTemplate(CreateTransactionTemplateCommand command) {
    this.transactionService.createTemplate(command);
  }

  @Override
  public void revertTransaction(RevertTransactionCommand command) {
    this.transactionService.revertTransaction(command);
  }
}
