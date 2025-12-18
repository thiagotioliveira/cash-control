package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionServiceImpl;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.transaction.TransactionEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.transaction.TransactionServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

  @Bean
  TransactionEventListener transactionEventListener(
      TransactionTemplateJpaRepository templateRepository, TransactionJpaRepository repository) {
    return new TransactionEventListener(templateRepository, repository);
  }

  @Bean
  TransactionService transactionService(
      TransactionJpaRepository repository,
      TransactionTemplateJpaRepository transactionTemplateJpaRepository) {
    return new TransactionServiceProxy(
        new TransactionServiceImpl(
            getTransactions(transactionRepository(repository, transactionTemplateJpaRepository))));
  }

  private TransactionRepository transactionRepository(
      TransactionJpaRepository repository,
      TransactionTemplateJpaRepository transactionTemplateJpaRepository) {
    return new TransactionRepositoryAdapter(repository, transactionTemplateJpaRepository);
  }

  private GetTransactions getTransactions(TransactionRepository transactionRepository) {
    return new GetTransactions(transactionRepository);
  }
}
