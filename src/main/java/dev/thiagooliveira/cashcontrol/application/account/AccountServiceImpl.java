package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final ConfirmTransaction confirmTransaction;
  private final CreateAccount createAccount;
  private final CreateDeposit createDeposit;
  private final CreateWithdrawal createWithdrawal;
  private final CreatePayable createPayable;
  private final CreateReceivable createReceivable;
  private final RevertTransaction revertTransaction;
  private final UpdateScheduledTransaction updateScheduledTransaction;

  public AccountServiceImpl(
      AccountRepository accountRepository,
      ConfirmTransaction confirmTransaction,
      CreateAccount createAccount,
      CreateDeposit createDeposit,
      CreateWithdrawal createWithdrawal,
      CreatePayable createPayable,
      CreateReceivable createReceivable,
      RevertTransaction revertTransaction,
      UpdateScheduledTransaction updateScheduledTransaction) {
    this.accountRepository = accountRepository;
    this.confirmTransaction = confirmTransaction;
    this.createAccount = createAccount;
    this.createDeposit = createDeposit;
    this.createWithdrawal = createWithdrawal;
    this.createPayable = createPayable;
    this.createReceivable = createReceivable;
    this.revertTransaction = revertTransaction;
    this.updateScheduledTransaction = updateScheduledTransaction;
  }

  @Override
  public void confirmTransaction(ConfirmTransactionCommand command) {
    this.confirmTransaction.execute(command);
  }

  @Override
  public void createDeposit(CreateTransactionCommand command) {
    this.createDeposit.execute(command);
  }

  @Override
  public void createWithdrawal(CreateTransactionCommand command) {
    this.createWithdrawal.execute(command);
  }

  @Override
  public void createPayable(CreateScheduledTransactionCommand command) {
    this.createPayable.execute(command);
  }

  @Override
  public void createReceivable(CreateScheduledTransactionCommand command) {
    this.createReceivable.execute(command);
  }

  @Override
  public void revertTransaction(RevertTransactionCommand command) {
    this.revertTransaction.execute(command);
  }

  @Override
  public void updateScheduledTransaction(UpdateScheduledTransactionCommand command) {
    this.updateScheduledTransaction.execute(command);
  }

  @Override
  public AccountSummary createAccount(CreateAccountCommand command) {
    return this.createAccount.execute(command);
  }

  @Override
  public Optional<AccountSummary> get(UUID organizationId, UUID accountId) {
    return this.accountRepository.findByOrganizationIdAndId(organizationId, accountId);
  }

  @Override
  public List<AccountSummary> get(UUID organizationId) {
    return this.accountRepository.findAllByOrganizationId(organizationId);
  }
}
