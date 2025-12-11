package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.domain.account.Account;
import dev.thiagooliveira.cashcontrol.domain.bank.Bank;
import java.text.DecimalFormat;

public class AccountModel {

  private final String name;
  private final String balance;
  private final BankModel bank;

  public AccountModel(DecimalFormat df, Account account, Bank bank) {
    this.name = account.getName();
    var symbol = bank.getCurrency().getSymbol();
    this.balance = symbol + " " + df.format(account.getBalance());
    this.bank = new BankModel(bank);
  }

  public String getName() {
    return name;
  }

  public String getBalance() {
    return balance;
  }

  public BankModel getBank() {
    return bank;
  }

  public static class BankModel {
    private final String name;
    private final String currency;
    private final String symbol;

    public BankModel(Bank bank) {
      this.name = bank.getName();
      this.currency = bank.getCurrency().getName();
      this.symbol = bank.getCurrency().getSymbol();
    }

    public String getName() {
      return name;
    }

    public String getCurrency() {
      return currency;
    }

    public String getSymbol() {
      return symbol;
    }
  }
}
