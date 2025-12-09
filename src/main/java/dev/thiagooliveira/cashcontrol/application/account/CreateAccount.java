package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.CreateAccountCommand;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.account.Account;

public class CreateAccount {

  private final EventStore eventStore;
  private final EventPublisher publisher;

  public CreateAccount(EventStore eventStore, EventPublisher publisher) {
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public Account execute(CreateAccountCommand command) {
    var account = Account.create(command.organizationId(), command.bankId(), command.name());
    var events = account.pendingEvents();

    eventStore.append(account.getId(), events, account.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    account.markEventsCommitted();
    return account;
  }
}
