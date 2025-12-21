package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.UpdateTransactionTemplateCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplate;

public class UpdateTransactionTemplate {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final TransactionRepository transactionRepository;

  public UpdateTransactionTemplate(
      EventStore eventStore,
      EventPublisher publisher,
      TransactionRepository transactionRepository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.transactionRepository = transactionRepository;
  }

  public void execute(UpdateTransactionTemplateCommand command) {
    var transaction =
        this.transactionRepository
            .findByOrganizationIdAndAccountIdAndId(
                command.organizationId(), command.accountId(), command.transactionId())
            .orElseThrow(() -> ApplicationException.notFound("Transaction not found"));

    if (!transaction.status().isScheduled()) {
      throw ApplicationException.badRequest("transaction must be scheduled");
    }

    var pastEvents =
        eventStore.load(command.organizationId(), transaction.transactionTemplateId().get());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("template not found");
    }

    var template = TransactionTemplate.rehydrate(pastEvents);
    template.update(
        command.userId(),
        command.amount(),
        command.description(),
        command.dueDate(),
        command.endDueDate());

    var newEvents = template.pendingEvents();
    eventStore.append(
        command.organizationId(),
        command.userId(),
        template.getId(),
        newEvents,
        template.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    template.markEventsCommitted();
  }
}
