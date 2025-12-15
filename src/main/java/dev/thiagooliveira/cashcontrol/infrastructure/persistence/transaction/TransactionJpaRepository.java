package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

  @Query(
      """
  SELECT
    CASE
      WHEN t.occurredAt = (
        SELECT MAX(t2.occurredAt)
        FROM TransactionEntity t2
        WHERE
          t2.organizationId = :organizationId
          AND t2.account.id = :accountId
          AND t2.status = :status
      )
      THEN true
      ELSE false
    END
  FROM TransactionEntity t
  WHERE
    t.organizationId = :organizationId
    AND t.account.id = :accountId
    AND t.id = :id
""")
  boolean isLatestTransaction(
      UUID organizationId, UUID accountId, UUID id, TransactionStatus status);

  boolean existsByTransactionTemplateIdAndOriginalDueDate(
      UUID transactionTemplateId, LocalDate originalDueDate);

  Optional<TransactionEntity> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  List<TransactionEntity> findAllByTransactionTemplateIdAndAccountId(
      UUID transactionTemplateId, UUID accountId);

  List<TransactionEntity> findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);
}
