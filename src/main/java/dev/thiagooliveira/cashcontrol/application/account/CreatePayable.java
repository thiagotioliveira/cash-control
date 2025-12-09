package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.CreateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.account.Account;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;

public class CreatePayable {

  private final CategoryRepository categoryRepository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public CreatePayable(
      CategoryRepository categoryRepository, EventStore eventStore, EventPublisher publisher) {
    this.categoryRepository = categoryRepository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public Account execute(CreateScheduledTransactionCommand command) {
    var category =
        categoryRepository
            .findByOrganizationIdAndId(command.organizationId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category not found"));
    if (!TransactionType.DEBIT.equals(category.getType()))
      throw ApplicationException.badRequest("category must be debit");
    var pastEvents = eventStore.load(command.accountId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("account not found");
    }

    if (command.recurrence().isNone() && command.installments().isPresent()) {
      throw ApplicationException.badRequest("installments can only be used with a recurrence");
    }

    var account = Account.rehydrate(pastEvents);

    account.payable(
        command.userId(),
        category.getId(),
        command.amount(),
        category.getName(),
        command.startDueDate(),
        command.recurrence(),
        command.installments());

    var newEvents = account.pendingEvents();
    eventStore.append(account.getId(), newEvents, account.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    account.markEventsCommitted();

    return account;
  }
}
