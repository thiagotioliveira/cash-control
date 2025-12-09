package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.shared.DueDateUtils;
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
  public Optional<GetTransactionItem> findByIdAndAccountId(UUID id, UUID accountId) {
    return this.repository.findByIdAndAccountId(id, accountId).map(TransactionEntity::toDomain);
  }

  @Override
  public List<GetTransactionItem> findAllByAccountIdAndDueDateBetween(
      UUID accountId, LocalDate startDate, LocalDate endDate) {
    var templates =
        this.templateRepository
            .findAllByAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
                accountId, startDate, endDate);
    templates.stream()
        //        .filter(t -> !t.getRecurrence().isNone())
        .forEach(
            t -> {
              var dueDate = t.getStartDate();
              if (!this.repository.existsByTransactionTemplateIdAndOriginalDueDate(
                  t.getId(), dueDate)) {
                while (dueDate.isBefore(endDate)) {
                  this.repository.save(new TransactionEntity(t, dueDate));
                  if (t.getRecurrence().isNone()) break;
                  dueDate = DueDateUtils.nextDueDate(dueDate, t.getRecurrence());
                }
              }
            });
    return this.repository
        .findAllByAccountIdAndDueDateBetweenOrderByDueDateAsc(accountId, startDate, endDate)
        .stream()
        .map(TransactionEntity::toDomain)
        .toList();
  }
}
