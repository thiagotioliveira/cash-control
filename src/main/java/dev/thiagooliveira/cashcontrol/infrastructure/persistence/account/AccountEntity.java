package dev.thiagooliveira.cashcontrol.infrastructure.persistence.account;

import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.*;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.TransactionConfirmed;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank.BankEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class AccountEntity {

  @Id private UUID id;

  @Column(nullable = false)
  private UUID organizationId;

  @Column(nullable = false)
  private Instant updatedAt;

  @Column(nullable = false)
  private String name;

  @ManyToOne(optional = false, fetch = FetchType.LAZY)
  @JoinColumn(name = "bank_id", nullable = false)
  private BankEntity bank;

  @Column(nullable = false)
  private BigDecimal balance;

  public AccountEntity() {}

  public AccountEntity(AccountCreated event) {
    this.id = event.accountId();
    this.updatedAt = event.occurredAt();
    this.name = event.name();
    this.balance = event.balance();
    this.bank = new BankEntity();
    this.bank.setId(event.bankId());
    this.organizationId = event.organizationId();
  }

  public void update(CreditApplied event) {
    this.balance = event.getBalanceAfter();
    this.updatedAt = event.occurredAt();
  }

  public void update(DebitApplied event) {
    this.balance = event.getBalanceAfter();
    this.updatedAt = event.occurredAt();
  }

  public void update(CreditReverted event) {
    this.balance = event.getBalanceAfter();
    this.updatedAt = event.occurredAt();
  }

  public void update(DebitReverted event) {
    this.balance = event.getBalanceAfter();
    this.updatedAt = event.occurredAt();
  }

  public void update(TransactionConfirmed event) {
    this.balance = event.balanceAfter();
    this.updatedAt = event.occurredAt();
  }

  public AccountSummary toDomain() {
    return new AccountSummary(
        this.id,
        this.name,
        new BankSummary(this.bank.getId(), this.bank.getName(), this.bank.getCurrency()),
        this.updatedAt,
        this.balance);
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BankEntity getBank() {
    return bank;
  }

  public void setBank(BankEntity bank) {
    this.bank = bank;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public void setOrganizationId(UUID organizationId) {
    this.organizationId = organizationId;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }
}
