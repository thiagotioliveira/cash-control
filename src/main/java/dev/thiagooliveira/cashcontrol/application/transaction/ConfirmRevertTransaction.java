package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.ConfirmRevertTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;

public class ConfirmRevertTransaction {

  private final EventStore eventStore;
  private final EventPublisher publisher;

  public ConfirmRevertTransaction(EventStore eventStore, EventPublisher publisher) {
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public void execute(ConfirmRevertTransactionCommand command) {
    var pastEvents = eventStore.load(command.organizationId(), command.transactionId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("transaction not found");
    }

    var transaction = Transaction.rehydrate(pastEvents);
    transaction.confirmRevert(command.userId());

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
