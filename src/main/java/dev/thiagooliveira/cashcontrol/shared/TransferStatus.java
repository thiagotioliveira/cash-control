package dev.thiagooliveira.cashcontrol.shared;

public enum TransferStatus {
  PENDING,
  STARTED,
  CONFIRMED;

  public boolean isPending() {
    return this == TransferStatus.PENDING;
  }

  public boolean isConfirmed() {
    return this == CONFIRMED;
  }
}
