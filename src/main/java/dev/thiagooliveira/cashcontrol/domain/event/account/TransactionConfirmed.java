package dev.thiagooliveira.cashcontrol.domain.event.account;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionConfirmed implements DomainEvent {
  private UUID organizationId;
  private UUID userId;
  private UUID accountId;
  private UUID transactionId;
  private TransactionType type;
  private BigDecimal amount;
  private BigDecimal balance;
  private Instant occurredAt;
  private int version;

  public TransactionConfirmed() {}

  public TransactionConfirmed(
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
    return getOccurredAt();
  }

  @Override
  public int version() {
    return getVersion();
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

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
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
