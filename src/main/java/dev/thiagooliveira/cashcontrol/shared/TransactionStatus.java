package dev.thiagooliveira.cashcontrol.shared;

public enum TransactionStatus {
  CONFIRMED,
  SCHEDULED;

  public boolean isScheduled() {
    return this == SCHEDULED;
  }

  public boolean isConfirmed() {
    return this == CONFIRMED;
  }
}
