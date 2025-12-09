package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.transaction.TransactionEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateJpaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

  @PersistenceContext private EntityManager em;

  @Bean
  TransactionTemplateJpaRepository transactionTemplateJpaRepository() {
    return new TransactionTemplateJpaRepository(em);
  }

  @Bean
  TransactionEventListener transactionEventListener(
      TransactionTemplateJpaRepository templateRepository, TransactionJpaRepository repository) {
    return new TransactionEventListener(templateRepository, repository);
  }

  @Bean
  TransactionRepository transactionRepository(
      TransactionJpaRepository repository,
      TransactionTemplateJpaRepository transactionTemplateJpaRepository) {
    return new TransactionRepositoryAdapter(repository, transactionTemplateJpaRepository);
  }

  @Bean
  GetTransactions getTransactions(TransactionRepository transactionRepository) {
    return new GetTransactions(transactionRepository);
  }
}
