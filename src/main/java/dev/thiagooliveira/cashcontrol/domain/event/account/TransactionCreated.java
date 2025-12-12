package dev.thiagooliveira.cashcontrol.domain.event.account;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionCreated implements DomainEvent {

  private UUID transactionId;
  private UUID accountId;
  private UUID categoryId;
  private TransactionType type;
  private BigDecimal amount;
  private BigDecimal balanceBefore;
  private BigDecimal balanceAfter;
  private String description;
  private UUID organizationId;
  private UUID userId;
  private Instant occurredAt;
  private int version;

  public TransactionCreated() {}

  public TransactionCreated(
      UUID transactionId,
      UUID accountId,
      UUID categoryId,
      TransactionType type,
      BigDecimal amount,
      BigDecimal balanceBefore,
      String description,
      UUID organizationId,
      UUID userId,
      Instant occurredAt,
      int version) {
    this.transactionId = transactionId;
    this.organizationId = organizationId;
    this.userId = userId;
    this.accountId = accountId;
    this.categoryId = categoryId;
    this.type = type;
    this.amount = amount;
    this.description = description;
    this.occurredAt = occurredAt;
    this.version = version;
    this.balanceBefore = balanceBefore;
  }

  @Override
  public UUID aggregateId() {
    return accountId;
  }

  @Override
  public Instant occurredAt() {
    return getOccurredAt();
  }

  @Override
  public int version() {
    return getVersion();
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public TransactionType getType() {
    return type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getBalanceBefore() {
    return balanceBefore;
  }

  public BigDecimal getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(BigDecimal balanceAfter) {
    this.balanceAfter = balanceAfter;
  }

  public String getDescription() {
    return description;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public int getVersion() {
    return version;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public UUID getUserId() {
    return userId;
  }
}
