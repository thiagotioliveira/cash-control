package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.domain.account.Account;
import dev.thiagooliveira.cashcontrol.domain.bank.Bank;
import java.text.DecimalFormat;

public class AccountModel {

  private final String name;
  private final String balance;

  public AccountModel(DecimalFormat df, Account account, Bank bank) {
    this.name = account.getName();
    var symbol = bank.getCurrency().getSymbol();
    this.balance = symbol + " " + df.format(account.getBalance());
  }

  public String getName() {
    return name;
  }

  public String getBalance() {
    return balance;
  }
}
