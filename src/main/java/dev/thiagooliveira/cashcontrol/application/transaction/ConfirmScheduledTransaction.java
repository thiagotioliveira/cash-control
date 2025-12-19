package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.ConfirmScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;

public class ConfirmScheduledTransaction {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final TransactionRepository transactionRepository;

  public ConfirmScheduledTransaction(
      EventStore eventStore,
      EventPublisher publisher,
      TransactionRepository transactionRepository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.transactionRepository = transactionRepository;
  }

  public void execute(ConfirmScheduledTransactionCommand command) {
    var pastEvents = eventStore.load(command.organizationId(), command.transactionId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("transaction not found");
    }

    if (this.transactionRepository.existsByOrganizationIdAndAccountIdAndOccurredAtAfter(
        command.organizationId(), command.accountId(), command.occurredAt())) {
      throw ApplicationException.badRequest("there are already more recent transactions");
    }

    var transaction = Transaction.rehydrate(pastEvents);
    transaction.confirmScheduled(command.userId(), command.occurredAt(), command.amount());

    var newEvents = transaction.pendingEvents();
    eventStore.append(
        command.organizationId(),
        transaction.getId(),
        newEvents,
        transaction.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    transaction.markEventsCommitted();
  }
}
