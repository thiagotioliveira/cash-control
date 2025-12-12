package dev.thiagooliveira.cashcontrol.domain.account;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.account.*;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Account {
  private UUID id;
  private UUID organizationId;
  private String name;
  private UUID bankId;
  private BigDecimal balance;
  private Instant updatedAt;

  private int version = 0;
  private final List<DomainEvent> pendingEvents = new ArrayList<>();

  private Account(
      UUID id,
      UUID organizationId,
      UUID bankId,
      String name,
      BigDecimal balance,
      Instant updatedAt) {
    this.id = id;
    this.organizationId = organizationId;
    this.bankId = bankId;
    this.name = name;
    this.balance = balance;
    this.updatedAt = updatedAt;
  }

  private Account(UUID organizationId, UUID bankId, String name, Instant updatedAt) {
    this(UUID.randomUUID(), organizationId, bankId, name, BigDecimal.ZERO, updatedAt);
  }

  public static Account create(UUID organizationId, UUID bankId, String name) {
    var account = new Account(organizationId, bankId, name, Instant.now());
    account.apply(
        new AccountCreated(
            account.id,
            account.name,
            account.bankId,
            account.balance,
            organizationId,
            account.updatedAt,
            1));
    return account;
  }

  public static Account restore(
      UUID id,
      UUID organizationId,
      UUID bankId,
      String name,
      BigDecimal balance,
      Instant updatedAt) {
    return new Account(id, organizationId, bankId, name, balance, updatedAt);
  }

  public static Account rehydrate(List<DomainEvent> events) {
    Account account = null;
    for (DomainEvent event : events) {
      if (event instanceof AccountCreated ac) {
        account = new Account(ac.accountId(), ac.bankId(), ac.name(), ac.occurredAt());
      } else if (account == null) {
        throw DomainException.badRequest("Account rehydration failed");
      }
      account.applyFromHistory(event);
    }
    return account;
  }

  public void credit(
      UUID userId, Instant occurredAt, UUID categoryId, BigDecimal amount, String description) {
    validate(amount);
    apply(
        new TransactionCreated(
            UUID.randomUUID(),
            id,
            categoryId,
            TransactionType.CREDIT,
            amount,
            balance,
            description,
            organizationId,
            userId,
            occurredAt,
            version + 1));
  }

  public void creditConfirmed(
      UUID userId, UUID transactionId, Instant occurredAt, BigDecimal amount) {
    validate(amount);
    apply(
        new TransactionConfirmed(
            organizationId,
            userId,
            id,
            transactionId,
            TransactionType.CREDIT,
            amount,
            balance,
            occurredAt,
            version + 1));
  }

  public void debit(
      UUID userId, Instant occurredAt, UUID categoryId, BigDecimal amount, String description) {
    validate(amount);
    apply(
        new TransactionCreated(
            UUID.randomUUID(),
            id,
            categoryId,
            TransactionType.DEBIT,
            amount,
            balance,
            description,
            organizationId,
            userId,
            occurredAt,
            version + 1));
  }

  public void debitConfirmed(
      UUID userId, UUID transactionId, Instant occurredAt, BigDecimal amount) {
    validate(amount);
    apply(
        new TransactionConfirmed(
            organizationId,
            userId,
            id,
            transactionId,
            TransactionType.DEBIT,
            amount,
            balance,
            occurredAt,
            version + 1));
  }

  public void payable(
      UUID userId,
      UUID categoryId,
      BigDecimal amount,
      String description,
      LocalDate startDueDate,
      Recurrence recurrence,
      Optional<Integer> installments) {
    validate(amount);
    validateDueDate(startDueDate);
    apply(
        new ScheduledTransactionCreated(
            UUID.randomUUID(),
            id,
            categoryId,
            TransactionType.DEBIT,
            amount,
            description,
            startDueDate,
            recurrence,
            installments.orElse(null),
            organizationId,
            userId,
            Instant.now(),
            version + 1));
  }

  public void receivable(
      UUID userId,
      UUID categoryId,
      BigDecimal amount,
      String description,
      LocalDate startDueDate,
      Recurrence recurrence,
      Optional<Integer> installments) {
    validate(amount);
    validateDueDate(startDueDate);
    apply(
        new ScheduledTransactionCreated(
            UUID.randomUUID(),
            id,
            categoryId,
            TransactionType.CREDIT,
            amount,
            description,
            startDueDate,
            recurrence,
            installments.orElse(null),
            organizationId,
            userId,
            Instant.now(),
            version + 1));
  }

  public void updateScheduledTransaction(
      UUID userId,
      UUID transactionId,
      BigDecimal amount,
      String description,
      LocalDate dueDate,
      Optional<LocalDate> endDueDate) {
    validate(amount);
    validateDueDate(dueDate);
    //    if (endDueDate.isPresent() && dueDayOfMonth != endDueDate.get().getDayOfMonth()) {
    //      throw DomainException.badRequest("Due date must be the same day of the month");
    //    }
    apply(
        new ScheduledTransactionUpdated(
            transactionId,
            id,
            amount,
            description,
            dueDate,
            endDueDate.orElse(null),
            organizationId,
            userId,
            Instant.now(),
            version + 1));
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

  public List<DomainEvent> pendingEvents() {
    return List.copyOf(pendingEvents);
  }

  public void markEventsCommitted() {
    pendingEvents.clear();
  }

  public int getVersion() {
    return version;
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

  private void applyFromHistory(DomainEvent e) {
    when(e);
    version++;
  }

  private void apply(DomainEvent event) {
    when(event);
    pendingEvents.add(event);
    version = event.version();
  }

  private void when(DomainEvent event) {
    switch (event) {
      case AccountCreated ev -> {
        id = ev.aggregateId();
        bankId = ev.bankId();
        name = ev.name();
        balance = ev.balance();
        organizationId = ev.organizationId();
        updatedAt = ev.occurredAt();
      }
      case TransactionCreated ev -> {
        if (ev.getType().isCredit()) {
          balance = balance.add(ev.getAmount());
          ev.setBalanceAfter(balance);
          updatedAt = ev.occurredAt();
        } else if (ev.getType().isDebit()) {
          balance = balance.subtract(ev.getAmount());
          ev.setBalanceAfter(balance);
          updatedAt = ev.occurredAt();
        } else throw DomainException.badRequest("unhandled transaction type " + ev.getType());
      }
      case ScheduledTransactionCreated ev -> {
        updatedAt = ev.occurredAt();
      }
      case TransactionConfirmed ev -> {
        if (ev.getType().isCredit()) {
          balance = balance.add(ev.getAmount());
          ev.setBalanceAfter(balance);
          updatedAt = ev.occurredAt();
        } else if (ev.getType().isDebit()) {
          balance = balance.subtract(ev.getAmount());
          ev.setBalanceAfter(balance);
          updatedAt = ev.occurredAt();
        } else throw DomainException.badRequest("unhandled transaction type " + ev.getType());
      }
      case ScheduledTransactionUpdated ev -> {
        updatedAt = ev.occurredAt();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }
}
