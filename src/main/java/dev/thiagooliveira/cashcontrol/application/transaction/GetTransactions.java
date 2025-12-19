package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionTemplateRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.Transaction;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplate;
import dev.thiagooliveira.cashcontrol.shared.DueDateUtils;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetTransactions {

  private final EventStore eventStore;
  private final EventPublisher publisher;
  private final TransactionTemplateRepository templateRepository;
  private final TransactionRepository repository;

  public GetTransactions(
      EventStore eventStore,
      EventPublisher publisher,
      TransactionTemplateRepository templateRepository,
      TransactionRepository repository) {
    this.eventStore = eventStore;
    this.publisher = publisher;
    this.templateRepository = templateRepository;
    this.repository = repository;
  }

  public List<TransactionSummary> execute(GetTransactionsCommand command) {
    var templates =
        this.templateRepository
            .findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
                command.organizationId(),
                command.accountId(),
                command.startDate(),
                command.endDate());
    templates.stream()
        .map(
            t -> {
              var pastEvents = eventStore.load(command.organizationId(), t.id());

              if (pastEvents.isEmpty()) {
                throw ApplicationException.notFound("template not found");
              }

              return TransactionTemplate.rehydrate(pastEvents);
            })
        .forEach(
            t -> {
              var dueDate =
                  command.startDate().withDayOfMonth(t.getOriginalStartDate().getDayOfMonth());
              if (!this.repository.existsByTransactionTemplateIdAndOriginalDueDate(
                  t.getId(), dueDate)) {
                while (dueDate.isBefore(command.endDate())) {
                  var transaction = Transaction.createScheduled(t, dueDate);
                  var events = transaction.pendingEvents();
                  eventStore.append(
                      command.organizationId(),
                      transaction.getId(),
                      events,
                      transaction.getVersion() - events.size());
                  events.forEach(publisher::publishEvent);
                  transaction.markEventsCommitted();

                  if (t.getRecurrence().isNone()) break;
                  dueDate = DueDateUtils.nextDueDate(dueDate, t.getRecurrence());
                }
              }
            });

    return this.repository.findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
        command.organizationId(), command.accountId(), command.startDate(), command.endDate());
  }

  public Optional<TransactionSummary> execute(GetTransactionCommand command) {
    return this.repository.findByOrganizationIdAndAccountIdAndId(
        command.organizationId(), command.accountId(), command.transactionId());
  }

  public boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id) {
    return this.repository.isLatestTransaction(organizationId, accountId, id);
  }
}
