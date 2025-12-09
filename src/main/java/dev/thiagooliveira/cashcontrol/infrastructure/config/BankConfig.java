package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.bank.CreateBank;
import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.bank.BankEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BankConfig {

  @Bean
  BankEventListener bankEventListener(BankJpaRepository repository) {
    return new BankEventListener(repository);
  }

  @Bean
  CreateBank createBank(
      BankRepository bankRepository, EventStore eventStore, EventPublisher publisher) {
    return new CreateBank(bankRepository, eventStore, publisher);
  }

  @Bean
  BankRepository bankRepository(BankJpaRepository repository) {
    return new BankRepositoryAdapter(repository);
  }
}
