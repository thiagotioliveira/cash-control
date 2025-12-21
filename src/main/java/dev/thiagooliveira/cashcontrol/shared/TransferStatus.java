package dev.thiagooliveira.cashcontrol.shared;

public enum TransferStatus {
  PENDING,
  IN_PROGRESS,
  PENDING_REVERT,
  CONFIRMED,
  DELETED;

  public boolean isPendingRevert() {
    return this == TransferStatus.PENDING_REVERT;
  }

  public boolean isPending() {
    return this == TransferStatus.PENDING;
  }

  public boolean isConfirmed() {
    return this == CONFIRMED;
  }
}
