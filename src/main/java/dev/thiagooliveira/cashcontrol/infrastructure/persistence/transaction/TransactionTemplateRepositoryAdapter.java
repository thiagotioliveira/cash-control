package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionTemplateRepository;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionTemplateSummary;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class TransactionTemplateRepositoryAdapter implements TransactionTemplateRepository {

  private final TransactionTemplateJpaRepository repository;

  public TransactionTemplateRepositoryAdapter(TransactionTemplateJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<TransactionTemplateSummary>
      findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate) {
    return this.repository
        .findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
            organizationId, accountId, startDate, endDate)
        .stream()
        .map(TransactionTemplateEntity::toDomain)
        .toList();
  }

  @Override
  public List<TransactionTemplateSummary>
      findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID organizationId, LocalDate startDate, LocalDate endDate) {
    return this.repository
        .findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
            organizationId, startDate, endDate)
        .stream()
        .map(TransactionTemplateEntity::toDomain)
        .toList();
  }
}
