package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.GetAccountItem;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.account.Account;
import java.util.UUID;

public class GetAccount {

  private final EventStore eventStore;
  private final EventPublisher publisher;

  public GetAccount(EventStore eventStore, EventPublisher publisher) {
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public GetAccountItem execute(UUID accountId) {
    var pastEvents = eventStore.load(accountId);

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    var account = Account.rehydrate(pastEvents);
    return new GetAccountItem(
        account.getId(),
        account.getName(),
        account.getBankId(),
        account.getUpdatedAt(),
        account.getBalance());
  }
}
