package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.account.*;
import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.bank.BankServiceImpl;
import dev.thiagooliveira.cashcontrol.application.bank.CreateBank;
import dev.thiagooliveira.cashcontrol.application.outbound.*;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.account.AccountEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.bank.BankEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.account.AccountServiceProxy;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.bank.BankServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

  @Bean
  BankEventListener bankEventListener(BankJpaRepository repository) {
    return new BankEventListener(repository);
  }

  @Bean
  BankService bankService(
      EventStore eventStore, EventPublisher publisher, BankJpaRepository repository) {
    var bankRepository = bankRepository(repository);
    return new BankServiceProxy(
        new BankServiceImpl(createBank(bankRepository, eventStore, publisher), bankRepository));
  }

  @Bean
  AccountEventListener accountEventListener(
      AccountService accountService, AccountJpaRepository repository) {
    return new AccountEventListener(accountService, repository);
  }

  @Bean
  AccountService accountService(
      EventStore eventStore,
      EventPublisher publisher,
      AccountJpaRepository accountJpaRepository,
      BankService bankService) {
    return new AccountServiceProxy(
        new AccountServiceImpl(
            accountRepository(accountJpaRepository),
            createAccount(eventStore, publisher, bankService),
            applyCredit(eventStore, publisher),
            applyDebit(eventStore, publisher),
            revertCredit(eventStore, publisher),
            revertDebit(eventStore, publisher)));
  }

  private CreateBank createBank(
      BankRepository bankRepository, EventStore eventStore, EventPublisher publisher) {
    return new CreateBank(bankRepository, eventStore, publisher);
  }

  private BankRepository bankRepository(BankJpaRepository repository) {
    return new BankRepositoryAdapter(repository);
  }

  private AccountRepository accountRepository(AccountJpaRepository repository) {
    return new AccountRepositoryAdapter(repository);
  }

  private CreateAccount createAccount(
      EventStore eventStore, EventPublisher publisher, BankService bankService) {
    return new CreateAccount(eventStore, publisher, bankService);
  }

  private ApplyCredit applyCredit(EventStore eventStore, EventPublisher publisher) {
    return new ApplyCredit(eventStore, publisher);
  }

  private ApplyDebit applyDebit(EventStore eventStore, EventPublisher publisher) {
    return new ApplyDebit(eventStore, publisher);
  }

  private RevertDebit revertDebit(EventStore eventStore, EventPublisher publisher) {
    return new RevertDebit(eventStore, publisher);
  }

  private RevertCredit revertCredit(EventStore eventStore, EventPublisher publisher) {
    return new RevertCredit(eventStore, publisher);
  }
}
