package dev.thiagooliveira.cashcontrol.infrastructure.listener.account;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.account.dto.ApplyCreditCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.ApplyDebitCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.RevertCreditCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.RevertDebitCommand;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.*;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.RevertTransactionRequested;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.ScheduledTransactionRequested;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionConfirmed;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionRequested;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountJpaRepository;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

public class AccountEventListener {

  private final AccountService accountService;
  private final AccountJpaRepository repository;

  public AccountEventListener(AccountService accountService, AccountJpaRepository repository) {
    this.accountService = accountService;
    this.repository = repository;
  }

  @EventListener
  public void on(AccountCreated event) {
    repository.save(new AccountEntity(event));
  }

  @Order(2)
  @EventListener
  public void on(TransactionRequested event) {
    if (event.type().isCredit()) {
      this.accountService.applyCredit(new ApplyCreditCommand(event));
    } else if (event.type().isDebit()) {
      this.accountService.applyDebit(new ApplyDebitCommand(event));
    } else throw InfrastructureException.badRequest("Type not supported");
  }

  @Order(2)
  @EventListener
  public void on(ScheduledTransactionRequested event) {
    if (event.type().isCredit()) {
      this.accountService.applyCredit(new ApplyCreditCommand(event));
    } else if (event.type().isDebit()) {
      this.accountService.applyDebit(new ApplyDebitCommand(event));
    } else throw InfrastructureException.badRequest("Type not supported");
  }

  @EventListener
  public void on(CreditApplied event) {
    var account = findById(event.getAccountId());
    account.update(event);
    this.repository.save(account);
  }

  @EventListener
  public void on(DebitApplied event) {
    var account = findById(event.getAccountId());
    account.update(event);
    this.repository.save(account);
  }

  @EventListener
  public void on(TransactionConfirmed event) {
    var entity = findById(event.accountId());
    entity.update(event);
    repository.save(entity);
  }

  @EventListener
  public void on(RevertTransactionRequested event) {
    if (event.getType().isCredit()) {
      this.accountService.revertCredit(new RevertCreditCommand(event));
    } else if (event.getType().isDebit()) {
      this.accountService.revertDebit(new RevertDebitCommand(event));
    } else throw InfrastructureException.badRequest("Type not supported");
  }

  @EventListener
  public void on(CreditReverted event) {
    var entity = findById(event.getAccountId());
    entity.update(event);
    repository.save(entity);
  }

  @EventListener
  public void on(DebitReverted event) {
    var entity = findById(event.getAccountId());
    entity.update(event);
    repository.save(entity);
  }

  private AccountEntity findById(UUID accountId) {
    return this.repository
        .findById(accountId)
        .orElseThrow(() -> InfrastructureException.notFound("Account not found"));
  }
}
