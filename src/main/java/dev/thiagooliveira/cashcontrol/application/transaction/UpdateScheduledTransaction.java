package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplate;

public class UpdateScheduledTransaction {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final TransactionRepository transactionRepository;

  public UpdateScheduledTransaction(
      EventStore eventStore,
      EventPublisher publisher,
      TransactionRepository transactionRepository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.transactionRepository = transactionRepository;
  }

  public void execute(UpdateScheduledTransactionCommand command) {
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
        template.getId(),
        newEvents,
        template.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    template.markEventsCommitted();

    transactionRepository
        .findAllByTransactionTemplateIdAndAccountId(template.getId(), command.accountId())
        .stream()
        .filter(t -> t.status().isScheduled())
        .forEach(
            t -> {
              var pastEventsTransaction =
                  eventStore.load(command.organizationId(), t.transactionId());

              if (pastEventsTransaction.isEmpty()) {
                throw ApplicationException.notFound("transaction not found");
              }

              var transactionScheduled = Transaction.rehydrate(pastEventsTransaction);
              transactionScheduled.update(
                  command.userId(), command.amount(), command.description(), command.dueDate());

              var newEventsTransaction = transactionScheduled.pendingEvents();
              eventStore.append(
                  command.organizationId(),
                  transactionScheduled.getId(),
                  newEventsTransaction,
                  transactionScheduled.getVersion() - newEventsTransaction.size());
              newEventsTransaction.forEach(publisher::publishEvent);

              transactionScheduled.markEventsCommitted();
            });
  }
}
