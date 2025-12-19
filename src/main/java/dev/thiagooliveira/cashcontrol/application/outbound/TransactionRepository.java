package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

  boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id);

  boolean existsByOrganizationIdAndAccountIdAndOccurredAtAfter(
      UUID organizationId, UUID accountId, Instant occurredAt);

  Optional<TransactionSummary> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  List<TransactionSummary> findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);

  List<TransactionSummary> findAllByTransactionTemplateIdAndAccountId(
      UUID transactionTemplateId, UUID accountId);

  boolean existsByTransactionTemplateIdAndOriginalDueDate(
      UUID transactionTemplateId, LocalDate originalDueDate);
}
