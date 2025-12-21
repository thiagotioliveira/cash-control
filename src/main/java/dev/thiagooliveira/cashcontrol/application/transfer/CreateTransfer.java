package dev.thiagooliveira.cashcontrol.application.transfer;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transfer;

public class CreateTransfer {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final CategoryService categoryService;

  public CreateTransfer(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.categoryService = categoryService;
  }

  public void execute(CreateTransferCommand command) {
    var category =
        categoryService
            .get(command.organizationId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category from not found"));
    if (!category.type().isTransfer()) {
      throw ApplicationException.notFound("category from is not transfer");
    }

    var transfer =
        Transfer.create(
            command.organizationId(),
            command.userId(),
            command.accountIdTo(),
            command.accountIdFrom(),
            command.categoryId(),
            command.occurredAt(),
            command.description(),
            command.amountFrom(),
            command.amountTo());
    var events = transfer.pendingEvents();
    eventStore.append(
        command.organizationId(),
        command.userId(),
        transfer.getId(),
        events,
        transfer.getVersion() - events.size());
    events.forEach(publisher::publishEvent);
    transfer.markEventsCommitted();
  }
}
