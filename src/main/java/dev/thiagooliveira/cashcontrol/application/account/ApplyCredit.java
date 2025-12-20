package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.ApplyCreditCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.account.Account;

public class ApplyCredit {

  private final EventStore eventStore;
  private final EventPublisher publisher;

  public ApplyCredit(EventStore eventStore, EventPublisher publisher) {
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public void execute(ApplyCreditCommand command) {
    var pastEvents = eventStore.load(command.organizationId(), command.accountId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    var account = Account.rehydrate(pastEvents);
    account.credit(
        command.transactionId(), command.userId(), command.amount(), command.occurredAt());

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
