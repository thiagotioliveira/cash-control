package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.RevertTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.domain.account.Account;

public class RevertTransaction {

  private final TransactionRepository transactionRepository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public RevertTransaction(
      TransactionRepository transactionRepository,
      EventStore eventStore,
      EventPublisher publisher) {
    this.transactionRepository = transactionRepository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public void execute(RevertTransactionCommand command) {
    var transaction =
        this.transactionRepository
            .findByOrganizationIdAndAccountIdAndId(
                command.organizationId(), command.accountId(), command.transactionId())
            .orElseThrow(() -> ApplicationException.notFound("transaction not found"));

    if (!transaction.status().isConfirmed()) {
      throw ApplicationException.badRequest("transaction must be confirmed");
    }

    if (!this.transactionRepository.isLatestTransaction(
        command.organizationId(), command.accountId(), command.transactionId())) {
      throw ApplicationException.badRequest("transaction must be the latest one");
    }

    var pastEvents = eventStore.load(command.organizationId(), command.accountId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    var account = Account.rehydrate(pastEvents);

    account.revertTransaction(
        command.userId(), command.transactionId(), transaction.amount(), transaction.type());

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
