package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AccountModel {

  private final List<String> activoBankNames = List.of("ActivoBank");

  private final List<String> milleniumBankNames = List.of("Millenium");

  private final UUID id;
  private final Instant updatedAt;
  private final String name;
  private final String bankName;
  private final String bankImg;
  private final String balance;

  public AccountModel(AccountSummary account) {
    this.id = account.id();
    this.updatedAt = account.updatedAt();
    this.name = account.name();
    var symbol = account.bank().currency().getSymbol();
    this.bankName = account.bank().name();
    if (activoBankNames.stream()
        .map(String::toLowerCase)
        .toList()
        .contains(this.bankName.toLowerCase())) {
      this.bankImg = "activobank.png";
    } else if (milleniumBankNames.stream()
        .map(String::toLowerCase)
        .toList()
        .contains(this.bankName.toLowerCase())) {
      this.bankImg = "millenium.png";
    } else {
      this.bankImg = "na.png";
    }
    this.balance = symbol + " " + df.format(account.balance());
  }

  public String getUpdatedAtFormatted() {
    return LocalDateTime.ofInstant(updatedAt, zoneId).format(dtfHourOfDay);
  }

  public String getName() {
    return name;
  }

  public String getBalance() {
    return balance;
  }

  public String getBankName() {
    return bankName;
  }

  public String getBankImg() {
    return bankImg;
  }

  public UUID getId() {
    return id;
  }
}
