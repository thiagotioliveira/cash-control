package dev.thiagooliveira.cashcontrol.domain.event.transaction.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class RevertTransactionRequested implements DomainEvent {
  private UUID organizationId;
  private UUID userId;
  private UUID accountId;
  private UUID transactionId;
  private TransactionType type;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private Instant occurredAt;
  private int version;

  public RevertTransactionRequested() {}

  public RevertTransactionRequested(
      UUID organizationId,
      UUID userId,
      UUID accountId,
      UUID transactionId,
      TransactionType type,
      BigDecimal amount,
      Instant occurredAt,
      int version) {
    this.organizationId = organizationId;
    this.userId = userId;
    this.accountId = accountId;
    this.transactionId = transactionId;
    this.type = type;
    this.amount = amount;
    this.occurredAt = occurredAt;
    this.version = version;
  }

  @Override
  public UUID aggregateId() {
    return accountId;
  }

  @Override
  public Instant occurredAt() {
    return occurredAt;
  }

  @Override
  public int version() {
    return version;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public TransactionType getType() {
    return type;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(BigDecimal balanceAfter) {
    this.balanceAfter = balanceAfter;
  }
}
