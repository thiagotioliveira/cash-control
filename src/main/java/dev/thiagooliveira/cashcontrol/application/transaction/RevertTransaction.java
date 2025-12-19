package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.RevertTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;

public class RevertTransaction {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final TransactionRepository transactionRepository;

  public RevertTransaction(
      EventStore eventStore,
      EventPublisher publisher,
      TransactionRepository transactionRepository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.transactionRepository = transactionRepository;
  }

  public void execute(RevertTransactionCommand command) {
    var t =
        this.transactionRepository
            .findByOrganizationIdAndAccountIdAndId(
                command.organizationId(), command.accountId(), command.transactionId())
            .orElseThrow(() -> ApplicationException.notFound("transaction not found"));

    if (!this.transactionRepository.isLatestTransaction(
        command.organizationId(), command.accountId(), command.transactionId())) {
      throw ApplicationException.badRequest("transaction must be the latest one");
    }

    var pastEvents = eventStore.load(command.organizationId(), command.transactionId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("transaction not found");
    }

    var transaction = Transaction.rehydrate(pastEvents);
    transaction.revertTransaction(command.userId());

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
