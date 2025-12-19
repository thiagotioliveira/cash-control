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
      TransactionJpaRepository repository,
      TransactionTemplateJpaRepository transactionTemplateJpaRepository,
      EventStore eventStore,
      EventPublisher eventPublisher,
      CategoryService categoryService) {
    var r = transactionRepository(repository);
    return new TransactionServiceProxy(
        new TransactionServiceImpl(
            getTransactions(
                eventStore,
                eventPublisher,
                transactionTemplateRepository(transactionTemplateJpaRepository),
                r),
            createDeposit(eventStore, eventPublisher, categoryService),
            createWithdrawal(eventStore, eventPublisher, categoryService),
            confirmTransaction(eventStore, eventPublisher),
            confirmScheduledTransaction(eventStore, eventPublisher),
            createPayable(eventStore, eventPublisher, categoryService),
            createReceivable(eventStore, eventPublisher, categoryService),
            updateScheduledTransaction(eventStore, eventPublisher, r),
            revertTransaction(eventStore, eventPublisher, r),
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

  private CreateDeposit createDeposit(
      EventStore eventStore, EventPublisher eventPublisher, CategoryService categoryService) {
    return new CreateDeposit(eventStore, eventPublisher, categoryService);
  }

  private CreateWithdrawal createWithdrawal(
      EventStore eventStore, EventPublisher eventPublisher, CategoryService categoryService) {
    return new CreateWithdrawal(eventStore, eventPublisher, categoryService);
  }

  private CreatePayable createPayable(
      EventStore eventStore, EventPublisher eventPublisher, CategoryService categoryService) {
    return new CreatePayable(eventStore, eventPublisher, categoryService);
  }

  private CreateReceivable createReceivable(
      EventStore eventStore, EventPublisher eventPublisher, CategoryService categoryService) {
    return new CreateReceivable(eventStore, eventPublisher, categoryService);
  }

  private ConfirmTransaction confirmTransaction(
      EventStore eventStore, EventPublisher eventPublisher) {
    return new ConfirmTransaction(eventStore, eventPublisher);
  }

  private ConfirmScheduledTransaction confirmScheduledTransaction(
      EventStore eventStore, EventPublisher eventPublisher) {
    return new ConfirmScheduledTransaction(eventStore, eventPublisher);
  }

  private RevertTransaction revertTransaction(
      EventStore eventStore,
      EventPublisher eventPublisher,
      TransactionRepository transactionRepository) {
    return new RevertTransaction(eventStore, eventPublisher, transactionRepository);
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
