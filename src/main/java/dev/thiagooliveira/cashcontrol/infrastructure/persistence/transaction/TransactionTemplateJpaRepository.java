package dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class TransactionTemplateJpaRepository {

  private final EntityManager em;

  public TransactionTemplateJpaRepository(EntityManager em) {
    this.em = em;
  }

  public TransactionTemplateEntity save(TransactionTemplateEntity entity) {
    return em.merge(entity);
  }

  public Optional<TransactionTemplateEntity> findByIdAndAccountId(UUID id, UUID accountId) {
    return em.createQuery(
            """
                              SELECT t
                              FROM TransactionTemplateEntity t
                              WHERE t.id = :id
                                AND t.accountId = :accountId
                              """,
            TransactionTemplateEntity.class)
        .setParameter("id", id)
        .setParameter("accountId", accountId)
        .getResultStream()
        .findFirst();
  }

  public List<TransactionTemplateEntity>
      findAllByAccountIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrEndDateIsNull(
          UUID accountId, LocalDate startDate, LocalDate endDate) {
    return em.createQuery(
            """
            SELECT t
            FROM TransactionTemplateEntity t
            WHERE t.accountId = :accountId
              AND t.startDate <= :endDate
              AND (
                    t.endDate >= :startDate
                    OR t.endDate IS NULL
                  )
            """,
            TransactionTemplateEntity.class)
        .setParameter("accountId", accountId)
        .setParameter("startDate", startDate)
        .setParameter("endDate", endDate)
        .getResultList();
  }
}
