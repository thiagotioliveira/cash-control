package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transfer.ConfirmTransfer;
import dev.thiagooliveira.cashcontrol.application.transfer.CreateTransfer;
import dev.thiagooliveira.cashcontrol.application.transfer.TransferService;
import dev.thiagooliveira.cashcontrol.application.transfer.TransferServiceImpl;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.transfer.TransferEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transfer.TransferJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.transfer.TransferServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransferConfig {

  @Bean
  public TransferEventListener transferEventListener(
      TransferService transferService, TransferJpaRepository transferJpaRepository) {
    return new TransferEventListener(transferService, transferJpaRepository);
  }

  @Bean
  public TransferService transferService(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new TransferServiceProxy(
        new TransferServiceImpl(
            createTransfer(eventStore, publisher, categoryService),
            confirmTransfer(eventStore, publisher)));
  }

  private CreateTransfer createTransfer(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    return new CreateTransfer(eventStore, publisher, categoryService);
  }

  private ConfirmTransfer confirmTransfer(EventStore eventStore, EventPublisher publisher) {
    return new ConfirmTransfer(eventStore, publisher);
  }
}
