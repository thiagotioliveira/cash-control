package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.CreateReceivableCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplate;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;

public class CreateReceivable {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final CategoryService categoryService;

  public CreateReceivable(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.categoryService = categoryService;
  }

  public void execute(CreateReceivableCommand command) {
    var category =
        categoryService
            .get(command.organizationId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category not found"));
    if (!TransactionType.CREDIT.equals(category.type()))
      throw ApplicationException.badRequest("category must be credit");

    var receivable =
        TransactionTemplate.createReceivable(
            command.organizationId(),
            command.accountId(),
            command.userId(),
            command.categoryId(),
            command.amount(),
            category.name(),
            command.startDueDate(),
            command.recurrence(),
            command.installments());
    var events = receivable.pendingEvents();
    eventStore.append(
        command.organizationId(),
        receivable.getId(),
        events,
        receivable.getVersion() - events.size());
    events.forEach(publisher::publishEvent);
    receivable.markEventsCommitted();
  }
}
