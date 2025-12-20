package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.*;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService {

  private final GetTransactions getTransactions;
  private final CreateTransaction createTransaction;
  private final ConfirmTransaction confirmTransaction;
  private final ConfirmScheduledTransaction confirmScheduledTransaction;
  private final CreateTransactionTemplate createTransactionTemplate;
  private final UpdateTransactionTemplate updateTransactionTemplate;
  private final UpdateScheduledTransaction updateScheduledTransaction;
  private final RevertTransaction revertTransaction;
  private final ConfirmRevertTransaction confirmRevertTransaction;

  public TransactionServiceImpl(
      GetTransactions getTransactions,
      CreateTransaction createTransaction,
      ConfirmTransaction confirmTransaction,
      ConfirmScheduledTransaction confirmScheduledTransaction,
      CreateTransactionTemplate createTransactionTemplate,
      UpdateTransactionTemplate updateTransactionTemplate,
      UpdateScheduledTransaction updateScheduledTransaction,
      RevertTransaction revertTransaction,
      ConfirmRevertTransaction confirmRevertTransaction) {
    this.getTransactions = getTransactions;
    this.createTransaction = createTransaction;
    this.confirmTransaction = confirmTransaction;
    this.confirmScheduledTransaction = confirmScheduledTransaction;
    this.createTransactionTemplate = createTransactionTemplate;
    this.updateTransactionTemplate = updateTransactionTemplate;
    this.updateScheduledTransaction = updateScheduledTransaction;
    this.revertTransaction = revertTransaction;
    this.confirmRevertTransaction = confirmRevertTransaction;
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

  @Override
  public void create(CreateTransactionCommand command) {
    this.createTransaction.execute(command);
  }

  @Override
  public void confirm(ConfirmTransactionCommand command) {
    this.confirmTransaction.execute(command);
  }

  @Override
  public void confirm(ConfirmScheduledTransactionCommand command) {
    this.confirmScheduledTransaction.execute(command);
  }

  @Override
  public void confirm(ConfirmRevertTransactionCommand command) {
    this.confirmRevertTransaction.execute(command);
  }

  @Override
  public void update(UpdateTransactionTemplateCommand command) {
    this.updateTransactionTemplate.execute(command);
  }

  @Override
  public void update(UpdateScheduledTransactionCommand command) {
    this.updateScheduledTransaction.execute(command);
  }

  @Override
  public void createTemplate(CreateTransactionTemplateCommand command) {
    this.createTransactionTemplate.execute(command);
  }

  @Override
  public void revertTransaction(RevertTransactionCommand command) {
    this.revertTransaction.execute(command);
  }
}
