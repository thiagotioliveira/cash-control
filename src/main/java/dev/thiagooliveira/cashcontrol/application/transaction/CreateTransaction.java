package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.CreateTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;

public class CreateTransaction {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final CategoryService categoryService;
  private final TransactionRepository transactionRepository;

  public CreateTransaction(
      EventStore eventStore,
      EventPublisher publisher,
      CategoryService categoryService,
      TransactionRepository transactionRepository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.categoryService = categoryService;
    this.transactionRepository = transactionRepository;
  }

  public void execute(CreateTransactionCommand command) {
    var category =
        categoryService
            .get(command.organizationId(), command.accountId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category not found"));
    if (!command.type().equals(category.type()))
      throw ApplicationException.badRequest("category must be " + category.type().name());
    if (this.transactionRepository.existsByOrganizationIdAndAccountIdAndOccurredAtAfter(
        command.organizationId(), command.accountId(), command.occurredAt())) {
      throw ApplicationException.badRequest("there are already more recent transactions");
    }
    var transaction =
        Transaction.create(
            command.organizationId(),
            command.accountId(),
            command.userId(),
            command.categoryId(),
            command.occurredAt(),
            command.description().orElse(command.type().isCredit() ? "Deposito" : "Retirada"),
            command.amount(),
            command.type(),
            command.transferId());
    var events = transaction.pendingEvents();
    eventStore.append(
        command.organizationId(),
        transaction.getId(),
        events,
        transaction.getVersion() - events.size());
    events.forEach(publisher::publishEvent);
    transaction.markEventsCommitted();
  }
}
