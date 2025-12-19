package dev.thiagooliveira.cashcontrol.domain.event.account.v1;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class CreditReverted implements DomainEvent {
  private UUID organizationId;
  private UUID accountId;
  private UUID transactionId;
  private UUID userId;
  private BigDecimal amount;
  private BigDecimal balanceAfter;
  private Instant occurredAt;
  private int version;

  public CreditReverted() {}

  public CreditReverted(
      UUID organizationId,
      UUID accountId,
      UUID transactionId,
      UUID userId,
      BigDecimal amount,
      Instant occurredAt,
      int version) {
    this.organizationId = organizationId;
    this.accountId = accountId;
    this.transactionId = transactionId;
    this.userId = userId;
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

  public BigDecimal getAmount() {
    return amount;
  }

  public UUID getTransactionId() {
    return transactionId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public BigDecimal getBalanceAfter() {
    return balanceAfter;
  }

  public void setBalanceAfter(BigDecimal balanceAfter) {
    this.balanceAfter = balanceAfter;
  }

  public UUID getUserId() {
    return userId;
  }
}
