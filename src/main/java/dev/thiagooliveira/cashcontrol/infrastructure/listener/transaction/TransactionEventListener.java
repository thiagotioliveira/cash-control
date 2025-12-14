package dev.thiagooliveira.cashcontrol.infrastructure.listener.transaction;

import dev.thiagooliveira.cashcontrol.domain.event.account.*;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateJpaRepository;
import java.util.UUID;
import org.springframework.context.event.EventListener;

public class TransactionEventListener {

  private final TransactionTemplateJpaRepository templateRepository;
  private final TransactionJpaRepository repository;

  public TransactionEventListener(
      TransactionTemplateJpaRepository templateRepository, TransactionJpaRepository repository) {
    this.templateRepository = templateRepository;
    this.repository = repository;
  }

  @EventListener
  public void on(TransactionCreated event) {
    repository.save(new TransactionEntity(event));
  }

  @EventListener
  public void on(ScheduledTransactionCreated event) {
    TransactionTemplateEntity entity = new TransactionTemplateEntity(event);
    templateRepository.save(entity);
  }

  @EventListener
  public void on(ScheduledTransactionUpdated event) {
    var transaction =
        findByIdAndAccountId(event.organizationId(), event.accountId(), event.transactionId());
    if (!transaction.getStatus().isScheduled()) {
      throw InfrastructureException.badRequest("Transaction must be scheduled");
    }
    var template =
        this.templateRepository
            .findByIdAndAccountId(transaction.getTransactionTemplate().getId(), event.accountId())
            .orElseThrow(() -> InfrastructureException.notFound("Transaction template not found"));

    template.update(event);
    this.templateRepository.save(template);
    var transactions =
        repository.findAllByTransactionTemplateIdAndAccountId(template.getId(), event.accountId());
    transactions.stream()
        .filter(t -> t.getStatus().isScheduled())
        //        .filter(
        //            t ->
        //                t.getOriginalDueDate().isAfter(transaction.getDueDate())
        //                    ||
        // t.getOriginalDueDate().equals(transactions.getFirst().getDueDate()))
        .forEach(
            (t -> {
              t.update(event);
              this.repository.save(t);
            }));
    if (event.endDueDate() != null) {
      transactions.stream()
          .filter(t -> t.getOriginalDueDate().isAfter(event.endDueDate()))
          .forEach(t -> this.repository.deleteById(t.getId()));
    }
  }

  @EventListener
  public void on(TransactionConfirmed event) {
    var transaction =
        findByIdAndAccountId(
            event.getOrganizationId(), event.getAccountId(), event.getTransactionId());
    transaction.confirm(event);
    this.repository.save(transaction);
  }

  @EventListener
  public void on(TransactionReversed event) {
    var transaction =
        findByIdAndAccountId(
            event.getOrganizationId(), event.getAccountId(), event.getTransactionId());
    if (transaction.wasScheduled()) {
      transaction.revert(event);
      this.repository.save(transaction);
    } else {
      this.repository.deleteById(transaction.getId());
    }
  }

  private TransactionEntity findByIdAndAccountId(
      UUID organizationId, UUID accountId, UUID transactionId) {
    return this.repository
        .findByOrganizationIdAndAccountIdAndId(organizationId, accountId, transactionId)
        .orElseThrow(() -> InfrastructureException.notFound("Transaction not found"));
  }
}
