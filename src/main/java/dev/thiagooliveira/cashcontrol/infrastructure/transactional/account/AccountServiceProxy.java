package dev.thiagooliveira.cashcontrol.infrastructure.transactional.account;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AccountServiceProxy implements AccountService {

  private final AccountService accountService;

  public AccountServiceProxy(AccountService accountService) {
    this.accountService = accountService;
  }

  @Override
  public void confirmTransaction(ConfirmTransactionCommand command) {
    this.accountService.confirmTransaction(command);
  }

  @Override
  public void createDeposit(CreateTransactionCommand command) {
    this.accountService.createDeposit(command);
  }

  @Override
  public void createWithdrawal(CreateTransactionCommand command) {
    this.accountService.createWithdrawal(command);
  }

  @Override
  public void createPayable(CreateScheduledTransactionCommand command) {
    this.accountService.createPayable(command);
  }

  @Override
  public void createReceivable(CreateScheduledTransactionCommand command) {
    this.accountService.createReceivable(command);
  }

  @Override
  public void revertTransaction(RevertTransactionCommand command) {
    this.accountService.revertTransaction(command);
  }

  @Override
  public void updateScheduledTransaction(UpdateScheduledTransactionCommand command) {
    this.accountService.updateScheduledTransaction(command);
  }

  @Override
  public AccountSummary createAccount(CreateAccountCommand command) {
    return this.accountService.createAccount(command);
  }

  @Override
  public Optional<AccountSummary> get(UUID organizationId, UUID accountId) {
    return this.accountService.get(organizationId, accountId);
  }

  @Override
  public List<AccountSummary> get(UUID organizationId) {
    return this.accountService.get(organizationId);
  }
}
