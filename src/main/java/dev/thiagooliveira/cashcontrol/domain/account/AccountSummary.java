package dev.thiagooliveira.cashcontrol.domain.account;

import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountSummary(
    UUID id, String name, UUID bankId, Currency currency, Instant updatedAt, BigDecimal balance) {

  public AccountSummary(Account account, BankSummary bank) {
    this(
        account.getId(),
        account.getName(),
        bank.id(),
        bank.currency(),
        account.getUpdatedAt(),
        account.getBalance());
  }
}
