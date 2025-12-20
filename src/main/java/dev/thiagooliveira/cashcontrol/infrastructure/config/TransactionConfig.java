package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionTemplateRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.*;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.transaction.TransactionEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.transaction.TransactionServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionConfig {

  @Bean
  TransactionEventListener transactionEventListener(
      TransactionService transactionService,
      TransactionTemplateJpaRepository templateRepository,
      TransactionJpaRepository repository) {
    return new TransactionEventListener(transactionService, templateRepository, repository);
  }

  @Bean
  TransactionService transactionService(
      TransactionJpaRepository transactionJpaRepository,
      TransactionTemplateJpaRepository transactionTemplateJpaRepository,
      EventStore eventStore,
      EventPublisher eventPublisher,
      CategoryService categoryService) {
    var transactionRepository = transactionRepository(transactionJpaRepository);
    return new TransactionServiceProxy(
        new TransactionServiceImpl(
            getTransactions(
                eventStore,
                eventPublisher,
                transactionTemplateRepository(transactionTemplateJpaRepository),
                transactionRepository),
            createTransaction(eventStore, eventPublisher, categoryService, transactionRepository),
            confirmTransaction(eventStore, eventPublisher),
            confirmScheduledTransaction(eventStore, eventPublisher, transactionRepository),
            createTransactionTemplate(eventStore, eventPublisher, categoryService),
            updateTransactionTemplate(eventStore, eventPublisher, transactionRepository),
            updateScheduledTransaction(eventStore, eventPublisher, transactionRepository),
            revertTransaction(eventStore, eventPublisher, transactionRepository),
            confirmRevertTransaction(eventStore, eventPublisher)));
  }

  private TransactionRepository transactionRepository(TransactionJpaRepository repository) {
    return new TransactionRepositoryAdapter(repository);
  }

  private TransactionTemplateRepository transactionTemplateRepository(
      TransactionTemplateJpaRepository repository) {
    return new TransactionTemplateRepositoryAdapter(repository);
  }

  private GetTransactions getTransactions(
      EventStore eventStore,
      EventPublisher eventPublisher,
      TransactionTemplateRepository templateRepository,
      TransactionRepository transactionRepository) {
    return new GetTransactions(
        eventStore, eventPublisher, templateRepository, transactionRepository);
  }

  private CreateTransaction createTransaction(
      EventStore eventStore,
      EventPublisher eventPublisher,
      CategoryService categoryService,
      TransactionRepository transactionRepository) {
    return new CreateTransaction(
        eventStore, eventPublisher, categoryService, transactionRepository);
  }

  private CreateTransactionTemplate createTransactionTemplate(
      EventStore eventStore, EventPublisher eventPublisher, CategoryService categoryService) {
    return new CreateTransactionTemplate(eventStore, eventPublisher, categoryService);
  }

  private ConfirmTransaction confirmTransaction(
      EventStore eventStore, EventPublisher eventPublisher) {
    return new ConfirmTransaction(eventStore, eventPublisher);
  }

  private ConfirmScheduledTransaction confirmScheduledTransaction(
      EventStore eventStore,
      EventPublisher eventPublisher,
      TransactionRepository transactionRepository) {
    return new ConfirmScheduledTransaction(eventStore, eventPublisher, transactionRepository);
  }

  private RevertTransaction revertTransaction(
      EventStore eventStore,
      EventPublisher eventPublisher,
      TransactionRepository transactionRepository) {
    return new RevertTransaction(eventStore, eventPublisher, transactionRepository);
  }

  private UpdateTransactionTemplate updateTransactionTemplate(
      EventStore eventStore,
      EventPublisher eventPublisher,
      TransactionRepository transactionRepository) {
    return new UpdateTransactionTemplate(eventStore, eventPublisher, transactionRepository);
  }

  private UpdateScheduledTransaction updateScheduledTransaction(
      EventStore eventStore,
      EventPublisher eventPublisher,
      TransactionRepository transactionRepository) {
    return new UpdateScheduledTransaction(eventStore, eventPublisher, transactionRepository);
  }

  private ConfirmRevertTransaction confirmRevertTransaction(
      EventStore eventStore, EventPublisher eventPublisher) {
    return new ConfirmRevertTransaction(eventStore, eventPublisher);
  }
}
