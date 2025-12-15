package dev.thiagooliveira.cashcontrol.domain.bank;

import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.util.UUID;

public record BankSummary(UUID id, String name, Currency currency) {
  public BankSummary(Bank bank) {
    this(bank.getId(), bank.getName(), bank.getCurrency());
  }
}
