package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.CreateTransactionTemplateCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplate;

public class CreateTransactionTemplate {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final CategoryService categoryService;

  public CreateTransactionTemplate(
      EventStore eventStore, EventPublisher publisher, CategoryService categoryService) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.categoryService = categoryService;
  }

  public void execute(CreateTransactionTemplateCommand command) {
    var category =
        categoryService
            .get(command.organizationId(), command.accountId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category not found"));
    if (!command.type().equals(category.type()))
      throw ApplicationException.badRequest("category must be " + category.type().name());

    var template =
        TransactionTemplate.create(
            command.organizationId(),
            command.accountId(),
            command.userId(),
            command.categoryId(),
            command.amount(),
            command.description().isEmpty() ? category.name() : command.description().get(),
            command.startDueDate(),
            command.recurrence(),
            command.installments(),
            command.type());
    var events = template.pendingEvents();
    eventStore.append(
        command.organizationId(), template.getId(), events, template.getVersion() - events.size());
    events.forEach(publisher::publishEvent);
    template.markEventsCommitted();
  }
}
