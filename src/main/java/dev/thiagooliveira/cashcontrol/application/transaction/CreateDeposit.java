package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.CreateDepositCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;

public class CreateDeposit {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final CategoryService categoryService;
  private final TransactionRepository transactionRepository;

  public CreateDeposit(
      EventStore eventStore,
      EventPublisher publisher,
      CategoryService categoryService,
      TransactionRepository transactionRepository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.categoryService = categoryService;
    this.transactionRepository = transactionRepository;
  }

  public void execute(CreateDepositCommand command) {
    var category =
        categoryService
            .get(command.organizationId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.notFound("category not found"));
    if (!TransactionType.CREDIT.equals(category.type()))
      throw ApplicationException.badRequest("category must be credit");
    if (this.transactionRepository.existsByOrganizationIdAndAccountIdAndOccurredAtAfter(
        command.organizationId(), command.accountId(), command.occurredAt())) {
      throw ApplicationException.badRequest("there are already more recent transactions");
    }
    var deposit =
        Transaction.createDeposit(
            command.organizationId(),
            command.accountId(),
            command.userId(),
            command.categoryId(),
            command.occurredAt(),
            command.description().orElse("Deposito"),
            command.amount());
    var events = deposit.pendingEvents();
    eventStore.append(
        command.organizationId(), deposit.getId(), events, deposit.getVersion() - events.size());
    events.forEach(publisher::publishEvent);
    deposit.markEventsCommitted();
  }
}
