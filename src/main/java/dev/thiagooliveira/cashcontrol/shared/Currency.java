package dev.thiagooliveira.cashcontrol.shared;

public enum Currency {
  EUR("€"),
  BRL("R$");

  private final String symbol;

  Currency(String symbol) {
    this.symbol = symbol;
  }

  public String symbol() {
    return this.symbol;
  }

  public String getName() {
    return this.name();
  }

  public String getSymbol() {
    return this.symbol;
  }
}
