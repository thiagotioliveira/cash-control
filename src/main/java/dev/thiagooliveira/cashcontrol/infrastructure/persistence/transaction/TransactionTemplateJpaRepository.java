package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionTemplateJpaRepository
    extends JpaRepository<TransactionTemplateEntity, UUID> {

  @Query(
      """
            SELECT t
            FROM TransactionTemplateEntity t
            WHERE t.organizationId = :organizationId
              AND t.accountId = :accountId
              AND t.startDate <= :endDate
              AND (
                    t.endDate >= :startDate
                    OR t.endDate IS NULL
                  )
            """)
  List<TransactionTemplateEntity>
      findAllByOrganizationIdAndAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID organizationId, UUID accountId, LocalDate startDate, LocalDate endDate);

  @Query(
      """
                  SELECT t
                  FROM TransactionTemplateEntity t
                  WHERE t.organizationId = :organizationId
                    AND t.startDate <= :endDate
                    AND (
                          t.endDate >= :startDate
                          OR t.endDate IS NULL
                        )
                  """)
  List<TransactionTemplateEntity>
      findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID organizationId, LocalDate startDate, LocalDate endDate);
}
