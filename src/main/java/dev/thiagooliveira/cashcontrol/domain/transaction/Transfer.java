package dev.thiagooliveira.cashcontrol.domain.transaction;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransferConfirmed;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransferRequested;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.shared.TransferStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Transfer extends Aggregate {
  private UUID id;
  private UUID organizationId;
  private UUID accountIdTo;
  private UUID accountIdFrom;
  private UUID categoryId;
  private String description;
  private Instant occurredAt;
  private BigDecimal amountFrom;
  private BigDecimal amountTo;
  private UUID userId;
  private TransferStatus status;

  private Transfer() {}

  public static Transfer create(
      UUID organizationId,
      UUID userId,
      UUID accountIdTo,
      UUID accountIdFrom,
      UUID categoryId,
      Instant occurredAt,
      String description,
      BigDecimal amountFrom,
      BigDecimal amountTo) {
    validateOccurredAt(occurredAt);
    validate(amountFrom);
    validate(amountTo);
    if (!amountFrom.equals(amountTo)) {
      throw DomainException.badRequest("Amount must be equal");
    }
    Transfer transfer = new Transfer();
    transfer.apply(
        new TransferRequested(
            UUID.randomUUID(),
            organizationId,
            userId,
            accountIdTo,
            accountIdFrom,
            categoryId,
            description,
            amountFrom,
            amountTo,
            occurredAt,
            1));
    return transfer;
  }

  public void confirm() {
    this.apply(new TransferConfirmed(id, occurredAt, getVersion() + 1));
  }

  public static Transfer rehydrate(List<DomainEvent> events) {
    Transfer transfer = null;
    for (DomainEvent event : events) {
      if (event instanceof TransferRequested dr) {
        transfer = new Transfer();
      } else if (transfer == null) {
        throw DomainException.badRequest("Transaction rehydration failed");
      }
      transfer.applyFromHistory(event);
    }
    return transfer;
  }

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case TransferRequested ev -> {
        this.id = ev.id();
        this.organizationId = ev.organizationId();
        this.accountIdTo = ev.accountIdTo();
        this.accountIdFrom = ev.accountIdFrom();
        this.categoryId = ev.categoryId();
        this.description = ev.description();
        this.occurredAt = ev.occurredAt();
        this.amountFrom = ev.amountFrom();
        this.amountTo = ev.amountTo();
        this.userId = ev.userId();
        this.status = TransferStatus.PENDING;
      }
      case TransferConfirmed ev -> {
        this.status = TransferStatus.CONFIRMED;
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
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

  public UUID getId() {
    return id;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public UUID getAccountIdTo() {
    return accountIdTo;
  }

  public UUID getAccountIdFrom() {
    return accountIdFrom;
  }

  public String getDescription() {
    return description;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public BigDecimal getAmountFrom() {
    return amountFrom;
  }

  public BigDecimal getAmountTo() {
    return amountTo;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public TransferStatus getStatus() {
    return status;
  }
}
