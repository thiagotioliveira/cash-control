package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.*;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionServiceImpl implements TransactionService {

  private final GetTransactions getTransactions;
  private final CreateDeposit createDeposit;
  private final CreateWithdrawal createWithdrawal;
  private final ConfirmTransaction confirmTransaction;
  private final ConfirmScheduledTransaction confirmScheduledTransaction;
  private final CreatePayable createPayable;
  private final CreateReceivable createReceivable;
  private final UpdateScheduledTransaction updateScheduledTransaction;
  private final RevertTransaction revertTransaction;
  private final ConfirmRevertTransaction confirmRevertTransaction;

  public TransactionServiceImpl(
      GetTransactions getTransactions,
      CreateDeposit createDeposit,
      CreateWithdrawal createWithdrawal,
      ConfirmTransaction confirmTransaction,
      ConfirmScheduledTransaction confirmScheduledTransaction,
      CreatePayable createPayable,
      CreateReceivable createReceivable,
      UpdateScheduledTransaction updateScheduledTransaction,
      RevertTransaction revertTransaction,
      ConfirmRevertTransaction confirmRevertTransaction) {
    this.getTransactions = getTransactions;
    this.createDeposit = createDeposit;
    this.createWithdrawal = createWithdrawal;
    this.confirmTransaction = confirmTransaction;
    this.confirmScheduledTransaction = confirmScheduledTransaction;
    this.createPayable = createPayable;
    this.createReceivable = createReceivable;
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
  public void createDeposit(CreateDepositCommand command) {
    this.createDeposit.execute(command);
  }

  @Override
  public void createWithdrawal(CreateWithdrawalCommand command) {
    this.createWithdrawal.execute(command);
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
  public void update(UpdateScheduledTransactionCommand command) {
    this.updateScheduledTransaction.execute(command);
  }

  @Override
  public void createPayable(CreatePayableCommand command) {
    this.createPayable.execute(command);
  }

  @Override
  public void createReceivable(CreateReceivableCommand command) {
    this.createReceivable.execute(command);
  }

  @Override
  public void revertTransaction(RevertTransactionCommand command) {
    this.revertTransaction.execute(command);
  }
}
