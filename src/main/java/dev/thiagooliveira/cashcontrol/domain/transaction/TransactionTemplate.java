package dev.thiagooliveira.cashcontrol.domain.transaction;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.PayableCreated;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.ReceivableCreated;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionTemplateUpdated;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionTemplate extends Aggregate {

  private UUID id;
  private UUID organizationId;
  private UUID accountId;
  private UUID categoryId;
  private UUID userId;
  private TransactionType type;
  private String description;
  private BigDecimal amount;
  private Recurrence recurrence;
  private LocalDate originalStartDate;
  private LocalDate startDate;
  private Integer totalInstallments;

  private TransactionTemplate() {}

  private TransactionTemplate(
      UUID id,
      UUID organizationId,
      UUID accountId,
      UUID categoryId,
      UUID userId,
      TransactionType type,
      String description,
      BigDecimal amount,
      Recurrence recurrence,
      LocalDate startDate,
      Integer totalInstallments) {
    this.id = id;
    this.organizationId = organizationId;
    this.accountId = accountId;
    this.categoryId = categoryId;
    this.userId = userId;
    this.type = type;
    this.description = description;
    this.amount = amount;
    this.recurrence = recurrence;
    this.startDate = startDate;
    this.totalInstallments = totalInstallments;
  }

  public static TransactionTemplate createPayable(
      UUID organizationId,
      UUID accountId,
      UUID userId,
      UUID categoryId,
      BigDecimal amount,
      String description,
      LocalDate startDueDate,
      Recurrence recurrence,
      Optional<Integer> installments) {
    validate(amount);
    var payable = new TransactionTemplate();
    payable.apply(
        new PayableCreated(
            UUID.randomUUID(),
            organizationId,
            accountId,
            userId,
            categoryId,
            amount,
            description,
            startDueDate,
            recurrence,
            installments.orElse(null),
            Instant.now(),
            1));
    return payable;
  }

  public static TransactionTemplate createReceivable(
      UUID organizationId,
      UUID accountId,
      UUID userId,
      UUID categoryId,
      BigDecimal amount,
      String description,
      LocalDate startDueDate,
      Recurrence recurrence,
      Optional<Integer> installments) {
    validate(amount);
    var receivable = new TransactionTemplate();
    receivable.apply(
        new ReceivableCreated(
            UUID.randomUUID(),
            organizationId,
            accountId,
            userId,
            categoryId,
            amount,
            description,
            startDueDate,
            recurrence,
            installments.orElse(null),
            Instant.now(),
            1));
    return receivable;
  }

  public void update(
      UUID userId,
      BigDecimal amount,
      String description,
      LocalDate dueDate,
      Optional<LocalDate> endDueDate) {
    validate(amount);
    validateDueDate(dueDate);
    this.apply(
        new TransactionTemplateUpdated(
            id,
            accountId,
            amount,
            description,
            dueDate,
            endDueDate.orElse(null),
            organizationId,
            userId,
            Instant.now(),
            getVersion() + 1));
  }

  public static TransactionTemplate rehydrate(List<DomainEvent> events) {
    TransactionTemplate template = null;
    for (DomainEvent event : events) {
      if (event instanceof PayableCreated pc) {
        template = new TransactionTemplate();
      } else if (event instanceof ReceivableCreated rc) {
        template = new TransactionTemplate();
      } else if (template == null) {
        throw DomainException.badRequest("Template rehydration failed");
      }
      template.applyFromHistory(event);
    }
    return template;
  }

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case PayableCreated ev -> {
        id = ev.templateId();
        organizationId = ev.organizationId();
        accountId = ev.accountId();
        userId = ev.userId();
        categoryId = ev.categoryId();
        startDate = ev.occurredAt().atZone(FormattersUtils.zoneId).toLocalDate();
        originalStartDate = startDate;
        description = ev.description();
        amount = ev.amount();
        type = TransactionType.DEBIT;
        recurrence = ev.recurrence();
        totalInstallments = ev.installments();
      }
      case ReceivableCreated ev -> {
        id = ev.templateId();
        organizationId = ev.organizationId();
        accountId = ev.accountId();
        userId = ev.userId();
        categoryId = ev.categoryId();
        startDate = ev.occurredAt().atZone(FormattersUtils.zoneId).toLocalDate();
        originalStartDate = startDate;
        description = ev.description();
        amount = ev.amount();
        type = TransactionType.CREDIT;
        recurrence = ev.recurrence();
        totalInstallments = ev.installments();
      }
      case TransactionTemplateUpdated ev -> {
        amount = ev.amount();
        description = ev.description();
        startDate = ev.dueDate();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }

  private static void validate(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw DomainException.badRequest("Amount must be a positive value");
    }
  }

  private static void validateDueDate(LocalDate dueDate) {
    if (LocalDate.now().isAfter(dueDate)) {
      throw DomainException.badRequest("Due date must be in the future");
    }
  }

  public UUID getId() {
    return id;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public UUID getUserId() {
    return userId;
  }

  public TransactionType getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Recurrence getRecurrence() {
    return recurrence;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public LocalDate getOriginalStartDate() {
    return originalStartDate;
  }

  public Integer getTotalInstallments() {
    return totalInstallments;
  }
}
