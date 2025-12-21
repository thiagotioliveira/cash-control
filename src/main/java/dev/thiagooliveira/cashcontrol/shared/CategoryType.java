package dev.thiagooliveira.cashcontrol.shared;

public enum CategoryType {
  DEBIT,
  CREDIT,
  TRANSFER;

  public boolean isTransfer() {
    return this == TRANSFER;
  }

  public boolean isDebit() {
    return this == DEBIT;
  }

  public boolean isCredit() {
    return this == CREDIT;
  }
}
