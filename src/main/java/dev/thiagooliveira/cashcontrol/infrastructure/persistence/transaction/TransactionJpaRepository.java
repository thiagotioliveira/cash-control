package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {
  boolean existsByTransactionTemplateIdAndOriginalDueDate(
      UUID transactionTemplateId, LocalDate originalDueDate);

  Page<TransactionEntity> findAllByAccountIdAndDueDateBetweenOrderByDueDateDesc(
      UUID accountId, LocalDate startDate, LocalDate endDate, Pageable pageable);

  Optional<TransactionEntity> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  List<TransactionEntity> findAllByTransactionTemplateIdAndAccountId(
      UUID transactionTemplateId, UUID accountId);

  List<TransactionEntity> findAllByOrganizationIdAndAccountIdAndDueDateBetweenOrderByDueDateDesc(
      UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);
}
