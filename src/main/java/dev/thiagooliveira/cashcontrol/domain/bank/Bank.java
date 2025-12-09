package dev.thiagooliveira.cashcontrol.domain.bank;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.domain.event.bank.BankCreated;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bank {
  private UUID id;
  private String name;
  private Currency currency;

  private int version = 0;
  private final List<DomainEvent> pendingEvents = new ArrayList<>();

  private Bank(UUID id, String name, Currency currency) {
    this.id = id;
    this.name = name;
    this.currency = currency;
  }

  private Bank(String name, Currency currency) {
    this(UUID.randomUUID(), name, currency);
  }

  public static Bank create(UUID organizationId, String name, Currency currency) {
    var bank = new Bank(name, currency);
    bank.apply(
        new BankCreated(bank.id, bank.name, bank.currency, organizationId, Instant.now(), 1));
    return bank;
  }

  public static Bank restore(UUID id, String name, Currency currency) {
    return new Bank(id, name, currency);
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Currency getCurrency() {
    return currency;
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

  private void apply(DomainEvent event) {
    when(event);
    pendingEvents.add(event);
    version = event.version();
  }

  private void when(DomainEvent event) {
    switch (event) {
      case BankCreated ev -> {
        id = ev.aggregateId();
        name = ev.name();
        currency = ev.currency();
      }
      default -> throw DomainException.badRequest("unhandled event " + event);
    }
  }
}
