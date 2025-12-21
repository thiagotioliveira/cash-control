package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transfer.*;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.transfer.TransferEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer.TransferJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.transfer.TransferServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransferConfig {

  @Bean
  public TransferEventListener transferEventListener(
      TransferService transferService,
      TransactionService transactionService,
      TransferJpaRepository transferJpaRepository,
      TransactionJpaRepository transactionJpaRepository) {
    return new TransferEventListener(
        transferService, transactionService, transferJpaRepository, transactionJpaRepository);
  }

  @Bean
  public TransferService transferService(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new TransferServiceProxy(
        new TransferServiceImpl(
            createTransfer(eventStore, publisher, categoryService),
            confirmTransfer(eventStore, publisher),
            revertTransfer(eventStore, publisher),
            confirmRevertTransfer(eventStore, publisher)));
  }

  private CreateTransfer createTransfer(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new CreateTransfer(eventStore, publisher, categoryService);
  }

  private ConfirmTransfer confirmTransfer(EventStore eventStore, EventPublisher publisher) {
    return new ConfirmTransfer(eventStore, publisher);
  }

  private RevertTransfer revertTransfer(EventStore eventStore, EventPublisher publisher) {
    return new RevertTransfer(eventStore, publisher);
  }

  private ConfirmRevertTransfer confirmRevertTransfer(
      EventStore eventStore, EventPublisher publisher) {
    return new ConfirmRevertTransfer(eventStore, publisher);
  }
}
