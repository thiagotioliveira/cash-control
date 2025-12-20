package dev.thiagooliveira.cashcontrol.domain.account;

import dev.thiagooliveira.cashcontrol.domain.Aggregate;
import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.account.v1.*;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.util.Strings;

public class Account extends Aggregate {
  private UUID id;
  private UUID organizationId;
  private String name;
  private UUID bankId;
  private BigDecimal balance;
  private Instant updatedAt;

  private Account() {}

  public static Account create(UUID organizationId, UUID bankId, String name) {
    if (Strings.isBlank(name)) {
      throw DomainException.badRequest("invalid name for account");
    }
    var account = new Account();
    account.apply(
        new AccountCreated(
            UUID.randomUUID(), name, bankId, BigDecimal.ZERO, organizationId, Instant.now(), 1));
    return account;
  }

  public static Account rehydrate(List<DomainEvent> events) {
    Account account = null;
    for (DomainEvent event : events) {
      if (event instanceof AccountCreated ac) {
        account = new Account();
      } else if (account == null) {
        throw DomainException.badRequest("Account rehydration failed");
      }
      account.applyFromHistory(event);
    }
    return account;
  }

  public void credit(UUID transactionId, UUID userId, BigDecimal amount, Instant occurredAt) {
    validate(amount);
    apply(
        new CreditApplied(
            organizationId, id, transactionId, userId, amount, occurredAt, getVersion() + 1));
  }

  public void revertCredit(UUID transactionId, UUID userId, BigDecimal amount) {
    validate(amount);
    apply(
        new CreditReverted(
            organizationId, id, transactionId, userId, amount, Instant.now(), getVersion() + 1));
  }

  public void debit(UUID transactionId, UUID userId, BigDecimal amount, Instant occurredAt) {
    validate(amount);
    apply(
        new DebitApplied(
            organizationId, id, transactionId, userId, amount, occurredAt, getVersion() + 1));
  }

  public void revertDebit(UUID transactionId, UUID userId, BigDecimal amount) {
    validate(amount);
    apply(
        new DebitReverted(
            organizationId, id, transactionId, userId, amount, Instant.now(), getVersion() + 1));
  }

  public UUID getId() {
    return id;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public String getName() {
    return name;
  }

  public UUID getBankId() {
    return bankId;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  private static void validateDueDate(LocalDate dueDate) {
    if (LocalDate.now().isAfter(dueDate)) {
      throw DomainException.badRequest("Due date must be in the future");
    }
  }

  private static void validate(BigDecimal amount) {
    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw DomainException.badRequest("Amount must be a positive value");
    }
  }

  @Override
  public UUID aggregateId() {
    return id;
  }

  @Override
  public void whenTemplate(DomainEvent event) {
    switch (event) {
      case AccountCreated ev -> {
        id = ev.aggregateId();
        bankId = ev.bankId();
        name = ev.name();
        balance = ev.balance();
        organizationId = ev.organizationId();
        updatedAt = ev.occurredAt();
      }
      case CreditApplied ev -> {
        balance = balance.add(ev.getAmount());
        ev.setBalanceAfter(balance);
        updatedAt = ev.occurredAt();
      }
      case DebitApplied ev -> {
        balance = balance.subtract(ev.getAmount());
        ev.setBalanceAfter(balance);
        updatedAt = ev.occurredAt();
      }
      case DebitReverted ev -> {
        balance = balance.add(ev.getAmount());
        ev.setBalanceAfter(balance);
        updatedAt = ev.occurredAt();
      }
      case CreditReverted ev -> {
        balance = balance.subtract(ev.getAmount());
        ev.setBalanceAfter(balance);
        updatedAt = ev.occurredAt();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }
}
