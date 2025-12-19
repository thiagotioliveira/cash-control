package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final CreateAccount createAccount;
  private final ApplyCredit applyCredit;
  private final ApplyDebit applyDebit;
  private final RevertCredit revertCredit;
  private final RevertDebit revertDebit;

  public AccountServiceImpl(
      AccountRepository accountRepository,
      CreateAccount createAccount,
      ApplyCredit applyCredit,
      ApplyDebit applyDebit,
      RevertCredit revertCredit,
      RevertDebit revertDebit) {
    this.accountRepository = accountRepository;
    this.createAccount = createAccount;
    this.applyCredit = applyCredit;
    this.applyDebit = applyDebit;
    this.revertCredit = revertCredit;
    this.revertDebit = revertDebit;
  }

  @Override
  public void applyCredit(ApplyCreditCommand command) {
    this.applyCredit.execute(command);
  }

  @Override
  public void revertCredit(RevertCreditCommand command) {
    this.revertCredit.execute(command);
  }

  @Override
  public void applyDebit(ApplyDebitCommand command) {
    this.applyDebit.execute(command);
  }

  @Override
  public void revertDebit(RevertDebitCommand command) {
    this.revertDebit.execute(command);
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
