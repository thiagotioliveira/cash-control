package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.shared.DueDateUtils;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionRepositoryAdapter implements TransactionRepository {

  private final TransactionJpaRepository repository;
  private final TransactionTemplateJpaRepository templateRepository;

  public TransactionRepositoryAdapter(
      TransactionJpaRepository repository, TransactionTemplateJpaRepository templateRepository) {
    this.repository = repository;
    this.templateRepository = templateRepository;
  }

  @Override
  public boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id) {
    return this.repository.isLatestTransaction(
        organizationId, accountId, id, TransactionStatus.CONFIRMED);
  }

  @Override
  public Optional<TransactionSummary> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id) {
    return this.repository
        .findByOrganizationIdAndAccountIdAndId(organizationId, accountId, id)
        .map(TransactionEntity::toDomain);
  }

  @Override
  public List<TransactionSummary>
      findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
          UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate) {
    populateTransactions(organizationId, accountId, startDate, endDate);
    return this.repository
        .findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
            organizationId, accountId, startDate, endDate)
        .stream()
        .map(TransactionEntity::toDomain)
        .toList();
  }

  private void populateTransactions(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate) {
    var templates =
        this.templateRepository
            .findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
                organizationId, accountId, startDate, endDate);
    templates.stream()
        //        .filter(t -> !t.getRecurrence().isNone())
        .forEach(
            t -> {
              var dueDate = startDate.withDayOfMonth(t.getOriginalStartDate().getDayOfMonth());
              if (!this.repository.existsByTransactionTemplateIdAndOriginalDueDate(
                  t.getId(), dueDate)) {
                while (dueDate.isBefore(endDate)) {
                  this.repository.save(new TransactionEntity(t, dueDate));
                  if (t.getRecurrence().isNone()) break;
                  dueDate = DueDateUtils.nextDueDate(dueDate, t.getRecurrence());
                }
              }
            });
  }
}
