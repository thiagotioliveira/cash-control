package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.CreatePayableCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplate;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;

public class CreatePayable {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final CategoryService categoryService;

  public CreatePayable(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.categoryService = categoryService;
  }

  public void execute(CreatePayableCommand command) {
    var category =
        categoryService
            .get(command.organizationId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category not found"));
    if (!TransactionType.DEBIT.equals(category.type()))
      throw ApplicationException.badRequest("category must be debit");

    var payable =
        TransactionTemplate.createPayable(
            command.organizationId(),
            command.accountId(),
            command.userId(),
            command.categoryId(),
            command.amount(),
            category.name(),
            command.startDueDate(),
            command.recurrence(),
            command.installments());
    var events = payable.pendingEvents();
    eventStore.append(
        command.organizationId(), payable.getId(), events, payable.getVersion() - events.size());
    events.forEach(publisher::publishEvent);
    payable.markEventsCommitted();
  }
}
