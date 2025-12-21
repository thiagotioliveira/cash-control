package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionRepositoryAdapter implements TransactionRepository {

  private final TransactionJpaRepository repository;

  public TransactionRepositoryAdapter(TransactionJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id) {
    return this.repository.isLatestTransaction(
        organizationId, accountId, id, TransactionStatus.CONFIRMED);
  }

  @Override
  public boolean existsByOrganizationIdAndAccountIdAndOccurredAtAfter(
      UUID organizationId, UUID accountId, Instant occurredAt) {
    return this.repository.existsByOrganizationIdAndAccount_IdAndOccurredAtAfter(
        organizationId, accountId, occurredAt);
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
    return this.repository
        .findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
            organizationId, accountId, startDate, endDate)
        .stream()
        .map(TransactionEntity::toDomain)
        .toList();
  }

  @Override
  public List<TransactionSummary> findAllByOrganizationIdAndDueDateBetweenOrderByDueDateDesc(
      UUID organizationId, LocalDate startDate, LocalDate endDate) {
    return this.repository
        .findAllByOrganizationIdAndDueDateBetweenOrderByDueDateDesc(
            organizationId, startDate, endDate)
        .stream()
        .map(TransactionEntity::toDomain)
        .toList();
  }

  @Override
  public List<TransactionSummary> findAllByTransactionTemplateIdAndAccountId(
      UUID transactionTemplateId, UUID accountId) {
    return this.repository
        .findAllByTransactionTemplateIdAndAccountId(transactionTemplateId, accountId)
        .stream()
        .map(TransactionEntity::toDomain)
        .toList();
  }

  @Override
  public boolean existsByTransactionTemplateIdAndOriginalDueDate(
      UUID transactionTemplateId, LocalDate originalDueDate) {
    return this.repository.existsByTransactionTemplateIdAndOriginalDueDate(
        transactionTemplateId, originalDueDate);
  }
}
