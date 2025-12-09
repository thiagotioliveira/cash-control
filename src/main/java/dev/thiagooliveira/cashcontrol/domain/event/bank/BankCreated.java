package dev.thiagooliveira.cashcontrol.domain.event.bank;

import dev.thiagooliveira.cashcontrol.domain.event.DomainEvent;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.time.Instant;
import java.util.UUID;

public record BankCreated(
    UUID bankId, String name, Currency currency, Instant occurredAt, int version)
    implements DomainEvent {

  @Override
  public UUID aggregateId() {
    return bankId;
  }
}
