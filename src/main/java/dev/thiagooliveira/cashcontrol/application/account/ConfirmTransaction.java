package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.ConfirmTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.domain.account.Account;
import java.time.Instant;

public class ConfirmTransaction {

  private final TransactionRepository transactionRepository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public ConfirmTransaction(
      TransactionRepository transactionRepository,
      EventStore eventStore,
      EventPublisher publisher) {
    this.transactionRepository = transactionRepository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public Account execute(ConfirmTransactionCommand command) {
    if (command.occurredAt().isAfter(Instant.now())) {
      throw ApplicationException.badRequest("occurredAt must be before now");
    }

    var transaction =
        this.transactionRepository
            .findByOrganizationIdAndAccountIdAndId(
                command.organizationId(), command.accountId(), command.transactionId())
            .orElseThrow(() -> ApplicationException.notFound("transaction not found"));

    if (!transaction.status().isScheduled()) {
      throw ApplicationException.badRequest("transaction must be scheduled");
    }

    var pastEvents = eventStore.load(command.organizationId(), command.accountId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    var account = Account.rehydrate(pastEvents);
    if (transaction.type().isDebit()) {
      account.debitConfirmed(
          command.userId(), transaction.transactionId(), command.occurredAt(), command.amount());
    } else if (transaction.type().isCredit()) {
      account.creditConfirmed(
          command.userId(), transaction.transactionId(), command.occurredAt(), command.amount());
    } else {
      throw ApplicationException.badRequest("transaction type not supported");
    }

    var newEvents = account.pendingEvents();
    eventStore.append(
        command.organizationId(),
        account.getId(),
        newEvents,
        account.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    account.markEventsCommitted();
    return account;
  }
}
