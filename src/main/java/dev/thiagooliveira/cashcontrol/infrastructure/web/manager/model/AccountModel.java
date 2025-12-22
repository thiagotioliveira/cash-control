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
  private final String bankImg1;
  private final String bankImg0;
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
      this.bankImg0 = "activobank.png";
        this.bankImg1 = "activobank-1.png";
    } else if (milleniumBankNames.stream()
        .map(String::toLowerCase)
        .toList()
        .contains(this.bankName.toLowerCase())) {
      this.bankImg0 = "millenium.png";
        this.bankImg1 = "millenium-1.png";
    } else {
      this.bankImg0 = "na.png";
        this.bankImg1 = "na.png";
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

  public String getBankImg1() {
    return bankImg1;
  }

  public String getBankImg0() {
      return bankImg0;
  }

  public UUID getId() {
    return id;
  }
}
