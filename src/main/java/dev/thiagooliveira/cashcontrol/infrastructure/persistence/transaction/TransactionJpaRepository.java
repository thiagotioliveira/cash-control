package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
  boolean existsByTransactionTemplateIdAndOriginalDueDate(
      UUID transactionTemplateId, LocalDate originalDueDate);

  List<TransactionEntity> findAllByAccountIdAndDueDateBetweenOrderByDueDateAsc(
      UUID accountId, LocalDate startDate, LocalDate endDate);

  Optional<TransactionEntity> findByIdAndAccountId(UUID id, UUID accountId);

  List<TransactionEntity> findAllByTransactionTemplateIdAndAccountId(
      UUID transactionTemplateId, UUID accountId);
}
