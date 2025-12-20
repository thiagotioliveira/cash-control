package dev.thiagooliveira.cashcontrol.domain.account;

import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountSummary(
    UUID id, String name, BankSummary bank, Instant updatedAt, BigDecimal balance) {

  public AccountSummary(Account account, BankSummary bank) {
    this(account.getId(), account.getName(), bank, account.getUpdatedAt(), account.getBalance());
  }
}
