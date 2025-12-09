package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.domain.account.Account;

public class UpdateScheduledTransaction {

  private final TransactionRepository transactionRepository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public UpdateScheduledTransaction(
      TransactionRepository transactionRepository,
      EventStore eventStore,
      EventPublisher publisher) {
    this.transactionRepository = transactionRepository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public Account execute(UpdateScheduledTransactionCommand command) {
    var transaction =
        this.transactionRepository
            .findByIdAndAccountId(command.transactionId(), command.accountId())
            .orElseThrow(() -> ApplicationException.notFound("transaction not found"));

    if (!transaction.status().isScheduled()) {
      throw ApplicationException.badRequest("transaction must be scheduled");
    }

    var pastEvents = eventStore.load(command.accountId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    var account = Account.rehydrate(pastEvents);

    account.updateScheduledTransaction(
        command.transactionId(), command.amount(), command.dueDayOfMonth(), command.endDueDate());

    var newEvents = account.pendingEvents();
    eventStore.append(account.getId(), newEvents, account.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    account.markEventsCommitted();
    return account;
  }
}
