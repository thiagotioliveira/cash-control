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
  private String name;
  private UUID bankId;
  private BigDecimal balance;

  private int version = 0;
  private final List<DomainEvent> pendingEvents = new ArrayList<>();

  private Account(UUID id, UUID bankId, String name) {
    this.id = id;
    this.bankId = bankId;
    this.name = name;
    this.balance = BigDecimal.ZERO;
  }

  private Account(UUID bankId, String name) {
    this(UUID.randomUUID(), bankId, name);
  }

  public static Account create(UUID bankId, String name) {
    var account = new Account(bankId, name);
    account.apply(
        new AccountCreated(
            account.id, account.name, account.bankId, account.balance, Instant.now(), 1));
    return account;
  }

  public static Account rehydrate(List<DomainEvent> events) {
    Account account = null;
    for (DomainEvent event : events) {
      if (event instanceof AccountCreated ac) {
        account = new Account(ac.accountId(), ac.bankId(), ac.name());
      } else if (account == null) {
        throw DomainException.badRequest("Account rehydration failed");
      }
      account.applyFromHistory(event);
    }
    return account;
  }

  public void credit(Instant occurredAt, UUID categoryId, BigDecimal amount, String description) {
    validate(amount);
    apply(
        new TransactionCreated(
            UUID.randomUUID(),
            id,
            categoryId,
            TransactionType.CREDIT,
            amount,
            description,
            occurredAt,
            version + 1));
  }

  public void creditConfirmed(UUID transactionId, Instant occurredAt, BigDecimal amount) {
    validate(amount);
    apply(
        new TransactionConfirmed(
            id, transactionId, TransactionType.CREDIT, amount, occurredAt, version + 1));
  }

  public void debit(Instant occurredAt, UUID categoryId, BigDecimal amount, String description) {
    validate(amount);
    apply(
        new TransactionCreated(
            UUID.randomUUID(),
            id,
            categoryId,
            TransactionType.DEBIT,
            amount,
            description,
            Instant.now(),
            version + 1));
  }

  public void debitConfirmed(UUID transactionId, Instant occurredAt, BigDecimal amount) {
    validate(amount);
    apply(
        new TransactionConfirmed(
            id, transactionId, TransactionType.DEBIT, amount, occurredAt, version + 1));
  }

  public void payable(
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
            Instant.now(),
            version + 1));
  }

  public void receivable(
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
            Instant.now(),
            version + 1));
  }

  public void updateScheduledTransaction(
      UUID transactionId, BigDecimal amount, int dueDayOfMonth, Optional<LocalDate> endDueDate) {
    validate(amount);
    if (endDueDate.isPresent() && dueDayOfMonth != endDueDate.get().getDayOfMonth()) {
      throw DomainException.badRequest("Due date must be the same day of the month");
    }
    apply(
        new ScheduledTransactionUpdated(
            transactionId,
            id,
            amount,
            dueDayOfMonth,
            endDueDate.orElse(null),
            Instant.now(),
            version + 1));
  }

  public UUID getId() {
    return id;
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
      }
      case TransactionCreated ev -> {
        if (ev.getType().isCredit()) {
          balance = balance.add(ev.getAmount());
          ev.setBalance(balance);
        } else if (ev.getType().isDebit()) {
          balance = balance.subtract(ev.getAmount());
          ev.setBalance(balance);
        } else throw DomainException.badRequest("unhandled transaction type " + ev.getType());
      }
      case ScheduledTransactionCreated ev -> {}
      case TransactionConfirmed ev -> {
        if (ev.getType().isCredit()) {
          balance = balance.add(ev.getAmount());
          ev.setBalance(balance);
        } else if (ev.getType().isDebit()) {
          balance = balance.subtract(ev.getAmount());
          ev.setBalance(balance);
        } else throw DomainException.badRequest("unhandled transaction type " + ev.getType());
      }
      case ScheduledTransactionUpdated ev -> {}
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }
}
