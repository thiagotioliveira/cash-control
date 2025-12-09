package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.account.*;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.account.AccountEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.account.AccountJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountConfig {

  @Bean
  AccountEventListener accountEventListener(AccountJpaRepository repository) {
    return new AccountEventListener(repository);
  }

  @Bean
  CreateAccount createAccount(EventStore eventStore, EventPublisher publisher) {
    return new CreateAccount(eventStore, publisher);
  }

  @Bean
  CreateDeposit createDeposit(
      CategoryRepository categoryRepository, EventStore eventStore, EventPublisher publisher) {
    return new CreateDeposit(categoryRepository, eventStore, publisher);
  }

  @Bean
  CreateWithdrawal createWithdrawal(
      CategoryRepository categoryRepository, EventStore eventStore, EventPublisher publisher) {
    return new CreateWithdrawal(categoryRepository, eventStore, publisher);
  }

  @Bean
  CreatePayable createPayable(
      CategoryRepository categoryRepository, EventStore eventStore, EventPublisher publisher) {
    return new CreatePayable(categoryRepository, eventStore, publisher);
  }

  @Bean
  CreateReceivable createReceivable(
      CategoryRepository categoryRepository, EventStore eventStore, EventPublisher publisher) {
    return new CreateReceivable(categoryRepository, eventStore, publisher);
  }

  @Bean
  ConfirmTransaction confirmTransaction(
      TransactionRepository transactionRepository,
      EventStore eventStore,
      EventPublisher publisher) {
    return new ConfirmTransaction(transactionRepository, eventStore, publisher);
  }

  @Bean
  UpdateScheduledTransaction updateScheduledTransaction(
      TransactionRepository transactionRepository,
      EventStore eventStore,
      EventPublisher publisher) {
    return new UpdateScheduledTransaction(transactionRepository, eventStore, publisher);
  }
}
