package dev.thiagooliveira.cashcontrol.infrastructure.listener.transaction;

import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.ConfirmRevertTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.ConfirmTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.CreateTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.CreditApplied;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.CreditReverted;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.DebitApplied;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.DebitReverted;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.*;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.transaction.TransactionTemplateJpaRepository;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

public class TransactionEventListener {

  private final TransactionService transactionService;
  private final TransactionTemplateJpaRepository templateRepository;
  private final TransactionJpaRepository repository;

  public TransactionEventListener(
      TransactionService transactionService,
      TransactionTemplateJpaRepository templateRepository,
      TransactionJpaRepository repository) {
    this.transactionService = transactionService;
    this.templateRepository = templateRepository;
    this.repository = repository;
  }

  @Order(2)
  @EventListener
  public void on(TransferRequested event) {
    this.transactionService.create(
        new CreateTransactionCommand(
            event.organizationId(),
            event.userId(),
            event.accountIdFrom(),
            event.occurredAt(),
            event.categoryIdFrom(),
            event.amountFrom(),
            event.description(),
            TransactionType.DEBIT,
            Optional.of(event.id())));
    this.transactionService.create(
        new CreateTransactionCommand(
            event.organizationId(),
            event.userId(),
            event.accountIdTo(),
            event.occurredAt(),
            event.categoryIdTo(),
            event.amountTo(),
            event.description(),
            TransactionType.CREDIT,
            Optional.of(event.id())));
  }

  @Order(1)
  @EventListener
  public void on(TransactionRequested event) {
    this.repository.save(new TransactionEntity(event));
  }

  @Order(1)
  @EventListener
  public void on(ScheduledTransactionRequested event) {
    var transaction =
        findByIdAndAccountId(event.organizationId(), event.accountId(), event.transactionId());
    transaction.updateStatusToPending();
    this.repository.save(transaction);
  }

  @EventListener
  public void on(CreditApplied event) {
    this.transactionService.confirm(new ConfirmTransactionCommand(event));
  }

  @EventListener
  public void on(DebitApplied event) {
    this.transactionService.confirm(new ConfirmTransactionCommand(event));
  }

  @EventListener
  public void on(TransactionConfirmed event) {
    var transaction =
        findByIdAndAccountId(event.organizationId(), event.accountId(), event.transactionId());
    transaction.confirm(event);
    this.repository.save(transaction);
  }

  @EventListener
  public void on(TransactionTemplateCreated event) {
    templateRepository.save(new TransactionTemplateEntity(event));
  }

  @EventListener
  public void on(ScheduledTransactionCreated event) {
    repository.save(new TransactionEntity(event));
  }

  @EventListener
  public void on(TransactionTemplateUpdated event) {
    var template =
        this.templateRepository
            .findById(event.templateId())
            .orElseThrow(() -> InfrastructureException.notFound("Template not found"));
    template.update(event);
    this.templateRepository.save(template);

    this.transactionService.update(new UpdateScheduledTransactionCommand(event));
  }

  @EventListener
  public void on(ScheduledTransactionUpdated event) {
    var transaction =
        findByIdAndAccountId(event.organizationId(), event.accountId(), event.transactionId());
    transaction.update(event);
    this.repository.save(transaction);
  }

  @EventListener
  public void on(TransactionReverted event) {
    var transaction =
        findByIdAndAccountId(event.organizationId(), event.accountId(), event.transactionId());
    if (transaction.wasScheduled()) {
      transaction.revert();
      this.repository.save(transaction);
    } else {
      this.repository.deleteById(transaction.getId());
    }
  }

  @EventListener
  public void on(CreditReverted event) {
    this.transactionService.confirm(new ConfirmRevertTransactionCommand(event));
  }

  @EventListener
  public void on(DebitReverted event) {
    this.transactionService.confirm(new ConfirmRevertTransactionCommand(event));
  }

  private TransactionEntity findByIdAndAccountId(
      UUID organizationId, UUID accountId, UUID transactionId) {
    return this.repository
        .findByOrganizationIdAndAccountIdAndId(organizationId, accountId, transactionId)
        .orElseThrow(() -> InfrastructureException.notFound("Transaction not found"));
  }
}
