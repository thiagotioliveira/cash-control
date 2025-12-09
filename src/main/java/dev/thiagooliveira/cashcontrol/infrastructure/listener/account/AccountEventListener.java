package dev.thiagooliveira.cashcontrol.infrastructure.listener.account;

import dev.thiagooliveira.cashcontrol.domain.event.account.AccountCreated;
import dev.thiagooliveira.cashcontrol.domain.event.account.TransactionConfirmed;
import dev.thiagooliveira.cashcontrol.domain.event.account.TransactionCreated;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountJpaRepository;
import java.util.UUID;
import org.springframework.context.event.EventListener;

public class AccountEventListener {

  private final AccountJpaRepository repository;

  public AccountEventListener(AccountJpaRepository repository) {
    this.repository = repository;
  }

  @EventListener
  public void on(AccountCreated event) {
    var entity = new AccountEntity(event);
    repository.save(entity);
  }

  @EventListener
  public void on(TransactionCreated event) {
    var entity = findById(event.getAccountId());
    entity.setBalance(event.getBalance());
    repository.save(entity);
  }

  @EventListener
  public void on(TransactionConfirmed event) {
    var entity = findById(event.getAccountId());
    entity.setBalance(event.getBalance());
    repository.save(entity);
  }

  private AccountEntity findById(UUID accountId) {
    return this.repository
        .findById(accountId)
        .orElseThrow(() -> InfrastructureException.notFound("Account not found"));
  }
}
