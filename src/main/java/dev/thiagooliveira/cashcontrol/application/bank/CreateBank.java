package dev.thiagooliveira.cashcontrol.application.bank;

import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.bank.Bank;

public class CreateBank {

  private final BankRepository repository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public CreateBank(BankRepository repository, EventStore eventStore, EventPublisher publisher) {
    this.repository = repository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public Bank execute(CreateBankCommand command) {
    if (repository.existsByOrganizationIdAndName(command.organizationId(), command.name()))
      throw ApplicationException.badRequest("bank already exists");
    var bank = Bank.create(command.organizationId(), command.name(), command.currency());
    var events = bank.pendingEvents();

    eventStore.append(bank.getId(), events, bank.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    bank.markEventsCommitted();
    return bank;
  }
}
