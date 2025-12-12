package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.account.dto.GetAccountItem;
import java.time.Instant;
import java.time.LocalDateTime;

public class AccountModel {

  private final Instant updatedAt;
  private final String name;
  private final String balance;

  public AccountModel(GetAccountItem account) {
    this.updatedAt = account.updatedAt();
    this.name = account.name();
    var symbol = account.currency().getSymbol();
    this.balance = symbol + " " + df.format(account.balance());
  }

  public String getNameFormatted() {
    return this.name + " (" + LocalDateTime.ofInstant(updatedAt, zoneId).format(dtfHourOfDay) + ")";
  }

  public String getName() {
    return name;
  }

  public String getBalance() {
    return balance;
  }
}
