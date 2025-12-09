package dev.thiagooliveira.cashcontrol.shared;

public enum TransactionType {
  DEBIT,
  CREDIT;

  public boolean isDebit() {
    return this == DEBIT;
  }

  public boolean isCredit() {
    return this == CREDIT;
  }
}
