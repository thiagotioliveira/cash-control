package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;

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
    var transactions =
        this.transactionRepository
            .findAllByTransactionTemplateIdAndAccountId(command.templateId(), command.accountId())
            .stream()
            .filter(transaction -> transaction.status().isScheduled())
            .toList();

    transactions.forEach(
        t -> {
          var pastEvents = eventStore.load(command.organizationId(), t.transactionId());

          if (pastEvents.isEmpty()) {
            throw ApplicationException.notFound("template not found");
          }

          var transaction = Transaction.rehydrate(pastEvents);
          transaction.update(
              command.userId(), command.amount(), command.description(), command.dueDay());

          var newEventsTransaction = transaction.pendingEvents();
          eventStore.append(
              command.organizationId(),
              command.userId(),
              transaction.getId(),
              newEventsTransaction,
              transaction.getVersion() - newEventsTransaction.size());
          newEventsTransaction.forEach(publisher::publishEvent);

          transaction.markEventsCommitted();
        });
  }
}
