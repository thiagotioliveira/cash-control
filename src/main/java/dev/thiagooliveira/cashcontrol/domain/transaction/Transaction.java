package dev.thiagooliveira.cashcontrol.domain.transaction;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.*;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.shared.FormattersUtils;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Transaction extends Aggregate {
  private UUID id;
  private UUID organizationId;
  private UUID accountId;
  private Optional<UUID> userId;
  private UUID categoryId;
  private Optional<UUID> transactionTemplateId;
  private Optional<Instant> occurredAt;
  private LocalDate originalDueDate;
  private LocalDate dueDate;
  private String description;
  private BigDecimal amount;
  private TransactionType type;
  private TransactionStatus status;
  private Optional<UUID> transferId;

  private Transaction() {
    this.transactionTemplateId = Optional.empty();
    this.occurredAt = Optional.empty();
  }

  public static Transaction create(
      UUID organizationId,
      UUID accountId,
      UUID userId,
      UUID categoryId,
      Instant occurredAt,
      String description,
      BigDecimal amount,
      TransactionType type,
      Optional<UUID> transferId) {
    validateOccurredAt(occurredAt);
    validate(amount);
    var transaction = new Transaction();
    transaction.apply(
        new TransactionRequested(
            UUID.randomUUID(),
            accountId,
            organizationId,
            userId,
            categoryId,
            description,
            amount,
            occurredAt,
            type,
            transferId,
            1));
    return transaction;
  }

  public static Transaction createScheduled(TransactionTemplate template, LocalDate dueDate) {
    var transaction = new Transaction();
    transaction.apply(
        new ScheduledTransactionCreated(
            UUID.randomUUID(),
            template.getAccountId(),
            template.getOrganizationId(),
            template.getId(),
            template.getCategoryId(),
            template.getType(),
            template.getDescription(),
            template.getAmount(),
            dueDate,
            Instant.now(),
            1));
    return transaction;
  }

  public void confirm(UUID userId, Instant occurredAt, BigDecimal balanceAfter) {
    validateOccurredAt(occurredAt);
    validate(amount);
    if (!status.isPending()) {
      throw DomainException.badRequest("transaction must be pending");
    }
    this.apply(
        new TransactionConfirmed(
            organizationId,
            accountId,
            id,
            transferId,
            userId,
            balanceAfter,
            occurredAt,
            getVersion() + 1));
  }

  public void confirmScheduled(UUID userId, Instant occurredAt, BigDecimal amount) {
    validateOccurredAt(occurredAt);
    validate(amount);
    if (!status.isScheduled()) {
      throw DomainException.badRequest("transaction must be scheduled");
    }
    this.apply(
        new ScheduledTransactionRequested(
            id,
            transactionTemplateId,
            accountId,
            organizationId,
            userId,
            categoryId,
            description,
            amount,
            Instant.now(),
            type,
            getVersion() + 1));
  }

  public void confirmRevert(UUID userId) {
    if (!status.isPendingReverted()) {
      throw DomainException.badRequest("transaction must be pending");
    }
    this.apply(
        new TransactionReverted(
            organizationId, accountId, id, userId, Instant.now(), getVersion() + 1));
  }

  public void revertTransaction(UUID userId) {
    if (!status.isConfirmed()) {
      throw DomainException.badRequest("transaction must be confirmed");
    }
    apply(
        new RevertTransactionRequested(
            organizationId, userId, accountId, id, type, amount, Instant.now(), getVersion() + 1));
  }

  public void update(UUID userId, BigDecimal amount, String description, int dueDay) {
    validate(amount);
    validateDueDay(dueDay);
    this.apply(
        new ScheduledTransactionUpdated(
            id,
            accountId,
            amount,
            description,
            dueDay,
            organizationId,
            userId,
            Instant.now(),
            getVersion() + 1));
  }

  public static Transaction rehydrate(List<DomainEvent> events) {
    Transaction transaction = null;
    for (DomainEvent event : events) {
      if (event instanceof TransactionRequested dr) {
        transaction = new Transaction();
      } else if (event instanceof ScheduledTransactionCreated stc) {
        transaction = new Transaction();
      } else if (transaction == null) {
        throw DomainException.badRequest("Transaction rehydration failed");
      }
      transaction.applyFromHistory(event);
    }
    return transaction;
  }

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case TransactionRequested ev -> {
        id = ev.transactionId();
        organizationId = ev.organizationId();
        accountId = ev.accountId();
        userId = Optional.of(ev.userId());
        categoryId = ev.categoryId();
        transactionTemplateId = Optional.empty();
        occurredAt = Optional.of(ev.occurredAt());
        originalDueDate = ev.occurredAt().atZone(FormattersUtils.zoneId).toLocalDate();
        dueDate = originalDueDate;
        description = ev.description();
        amount = ev.amount();
        type = ev.type();
        status = TransactionStatus.PENDING;
        transferId = ev.transferId();
      }
      case TransactionConfirmed ev -> {
        status = TransactionStatus.CONFIRMED;
        userId = Optional.of(ev.userId());
      }
      case ScheduledTransactionCreated ev -> {
        id = ev.transactionId();
        organizationId = ev.organizationId();
        userId = Optional.empty();
        accountId = ev.accountId();
        categoryId = ev.categoryId();
        transactionTemplateId = Optional.of(ev.templateId());
        occurredAt = Optional.of(ev.occurredAt());
        originalDueDate = ev.dueDate();
        dueDate = originalDueDate;
        description = ev.description();
        amount = ev.amount();
        type = ev.type();
        status = TransactionStatus.SCHEDULED;
      }
      case ScheduledTransactionRequested ev -> {
        status = TransactionStatus.PENDING;
      }
      case ScheduledTransactionUpdated ev -> {
        amount = ev.amount();
        description = ev.description();
        dueDate = dueDate.withDayOfMonth(ev.dueDay());
      }
      case RevertTransactionRequested ev -> {
        status = TransactionStatus.PENDING_REVERTED;
      }
      case TransactionReverted ev -> {
        if (transactionTemplateId.isEmpty()) {
          status = TransactionStatus.DELETED;
        } else {
          status = TransactionStatus.SCHEDULED;
        }
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }

  public UUID getId() {
    return id;
  }

  public Optional<UUID> getTransactionTemplateId() {
    return transactionTemplateId;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  private static void validate(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw DomainException.badRequest("Amount must be a positive value");
    }
  }

  public static void validateOccurredAt(Instant occurredAt) {
    if (occurredAt.isAfter(Instant.now())) {
      throw DomainException.badRequest("occurredAt must be before now");
    }
  }

  private static void validateDueDate(LocalDate dueDate) {
    if (LocalDate.now().isAfter(dueDate)) {
      throw DomainException.badRequest("Due date must be in the future");
    }
  }

  private static void validateDueDay(int dueDay) {
    if (dueDay < 1 || dueDay > 31) {
      throw DomainException.badRequest("invalid due day");
    }
  }
}
