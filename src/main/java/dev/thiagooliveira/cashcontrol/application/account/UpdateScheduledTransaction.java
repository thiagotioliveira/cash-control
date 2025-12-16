package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.account.Account;

public class UpdateScheduledTransaction {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final TransactionService transactionService;

  public UpdateScheduledTransaction(
      EventStore eventStore, EventPublisher publisher, TransactionService transactionService) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.transactionService = transactionService;
  }

  public void execute(UpdateScheduledTransactionCommand command) {
    var transaction =
        this.transactionService
            .get(
                new GetTransactionCommand(
                    command.organizationId(), command.accountId(), command.transactionId()))
            .orElseThrow(() -> ApplicationException.notFound("transaction not found"));

    if (!transaction.status().isScheduled()) {
      throw ApplicationException.badRequest("transaction must be scheduled");
    }

    var pastEvents = eventStore.load(command.organizationId(), command.accountId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    var account = Account.rehydrate(pastEvents);

    account.updateScheduledTransaction(
        command.userId(),
        command.transactionId(),
        command.amount(),
        command.description(),
        command.dueDate(),
        command.endDueDate());

    var newEvents = account.pendingEvents();
    eventStore.append(
        command.organizationId(),
        account.getId(),
        newEvents,
        account.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    account.markEventsCommitted();
  }
}
