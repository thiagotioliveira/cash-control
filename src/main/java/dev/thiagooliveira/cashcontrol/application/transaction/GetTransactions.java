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
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplateSummary;
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
    var templates = loadTemplates(command);

    for (var templateEntity : templates) {
      var template = rehydrateTemplate(command.organizationId(), templateEntity.id());
      generateTransactions(command, template);
    }

    return loadTransactions(command);
  }

  public Optional<TransactionSummary> execute(GetTransactionCommand command) {
    return repository.findByOrganizationIdAndAccountIdAndId(
        command.organizationId(), command.accountId(), command.transactionId());
  }

  public boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id) {
    return repository.isLatestTransaction(organizationId, accountId, id);
  }

  // ------------------- MÉTODOS AUXILIARES -------------------

  private List<TransactionTemplateSummary> loadTemplates(GetTransactionsCommand command) {
    if (command.accountId().isPresent()) {
      UUID accountId = command.accountId().get();
      return templateRepository
          .findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
              command.organizationId(), accountId, command.startDate(), command.endDate());
    }

    return templateRepository
        .findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
            command.organizationId(), command.startDate(), command.endDate());
  }

  private TransactionTemplate rehydrateTemplate(UUID organizationId, UUID templateId) {
    var pastEvents = eventStore.load(organizationId, templateId);

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("template not found");
    }

    return TransactionTemplate.rehydrate(pastEvents);
  }

  private void generateTransactions(GetTransactionsCommand command, TransactionTemplate template) {
    var dueDate =
        command.startDate().withDayOfMonth(template.getOriginalStartDate().getDayOfMonth());

    if (repository.existsByTransactionTemplateIdAndOriginalDueDate(template.getId(), dueDate)) {
      return;
    }

    while (!dueDate.isAfter(command.endDate())) {
      createAndPublishTransaction(command.organizationId(), template, dueDate);

      if (template.getRecurrence().isNone()) {
        break;
      }

      dueDate = DueDateUtils.nextDueDate(dueDate, template.getRecurrence());
    }
  }

  private void createAndPublishTransaction(
      UUID organizationId, TransactionTemplate template, java.time.LocalDate dueDate) {
    var transaction = Transaction.createScheduled(template, dueDate);
    var events = transaction.pendingEvents();

    eventStore.append(
        organizationId, transaction.getId(), events, transaction.getVersion() - events.size());

    events.forEach(publisher::publishEvent);
    transaction.markEventsCommitted();
  }

  private List<TransactionSummary> loadTransactions(GetTransactionsCommand command) {
    if (command.accountId().isPresent()) {
      UUID accountId = command.accountId().get();
      return repository.findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
          command.organizationId(), accountId, command.startDate(), command.endDate());
    }

    return repository.findAllByOrganizationIdAndDueDateBetweenOrderByDueDateDesc(
        command.organizationId(), command.startDate(), command.endDate());
  }
}
