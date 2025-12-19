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
  public void applyCredit(ApplyCreditCommand command) {
    this.accountService.applyCredit(command);
  }

  @Override
  public void revertCredit(RevertCreditCommand command) {
    this.accountService.revertCredit(command);
  }

  @Override
  public void applyDebit(ApplyDebitCommand command) {
    this.accountService.applyDebit(command);
  }

  @Override
  public void revertDebit(RevertDebitCommand command) {
    this.accountService.revertDebit(command);
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
