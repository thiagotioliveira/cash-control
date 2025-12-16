package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.CreateAccountCommand;
import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.account.Account;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;

public class CreateAccount {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final BankService bankService;

  public CreateAccount(EventStore eventStore, EventPublisher publisher, BankService bankService) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.bankService = bankService;
  }

  public AccountSummary execute(CreateAccountCommand command) {
    var bank =
        bankService
            .get(command.organizationId(), command.bankId())
            .orElseThrow(() -> ApplicationException.notFound("bank not found"));
    var account = Account.create(command.organizationId(), command.bankId(), command.name());
    var events = account.pendingEvents();

    eventStore.append(
        command.organizationId(), account.getId(), events, account.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    account.markEventsCommitted();
    return new AccountSummary(account, bank);
  }
}
