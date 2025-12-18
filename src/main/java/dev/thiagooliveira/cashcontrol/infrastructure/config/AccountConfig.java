package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.account.*;
import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.bank.BankServiceImpl;
import dev.thiagooliveira.cashcontrol.application.bank.CreateBank;
import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.outbound.*;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
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
  AccountEventListener accountEventListener(AccountJpaRepository repository) {
    return new AccountEventListener(repository);
  }

  @Bean
  AccountService accountService(
      EventStore eventStore,
      EventPublisher publisher,
      AccountJpaRepository accountJpaRepository,
      BankService bankService,
      CategoryService categoryService,
      TransactionService transactionService) {
    return new AccountServiceProxy(
        new AccountServiceImpl(
            accountRepository(accountJpaRepository),
            confirmTransaction(eventStore, publisher, transactionService),
            createAccount(eventStore, publisher, bankService),
            createDeposit(eventStore, publisher, categoryService),
            createWithdrawal(eventStore, publisher, categoryService),
            createPayable(eventStore, publisher, categoryService),
            createReceivable(eventStore, publisher, categoryService),
            revertTransaction(eventStore, publisher, transactionService),
            updateScheduledTransaction(eventStore, publisher, transactionService)));
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

  private CreateDeposit createDeposit(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new CreateDeposit(eventStore, publisher, categoryService);
  }

  private CreateWithdrawal createWithdrawal(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new CreateWithdrawal(eventStore, publisher, categoryService);
  }

  private CreatePayable createPayable(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new CreatePayable(eventStore, publisher, categoryService);
  }

  private CreateReceivable createReceivable(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new CreateReceivable(eventStore, publisher, categoryService);
  }

  private ConfirmTransaction confirmTransaction(
      EventStore eventStore, EventPublisher publisher, TransactionService transactionService) {
    return new ConfirmTransaction(eventStore, publisher, transactionService);
  }

  private UpdateScheduledTransaction updateScheduledTransaction(
      EventStore eventStore, EventPublisher publisher, TransactionService transactionService) {
    return new UpdateScheduledTransaction(eventStore, publisher, transactionService);
  }

  private RevertTransaction revertTransaction(
      EventStore eventStore, EventPublisher publisher, TransactionService transactionService) {
    return new RevertTransaction(eventStore, publisher, transactionService);
  }
}
