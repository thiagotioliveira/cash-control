package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.CreateTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.account.Account;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.time.Instant;

public class CreateWithdrawal {

  private final CategoryRepository categoryRepository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public CreateWithdrawal(
      CategoryRepository categoryRepository, EventStore eventStore, EventPublisher publisher) {
    this.categoryRepository = categoryRepository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public Account execute(CreateTransactionCommand command) {
    if (command.occurredAt().isAfter(Instant.now())) {
      throw ApplicationException.badRequest("occurredAt must be before now");
    }
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

    var account = Account.rehydrate(pastEvents);
    account.debit(
        command.userId(),
        command.occurredAt(),
        category.getId(),
        command.amount(),
        command.description().orElse("Retirada"));

    var newEvents = account.pendingEvents();
    eventStore.append(account.getId(), newEvents, account.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    account.markEventsCommitted();

    return account;
  }
}
