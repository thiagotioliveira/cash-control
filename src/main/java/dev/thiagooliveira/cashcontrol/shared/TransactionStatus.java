package dev.thiagooliveira.cashcontrol.shared;

public enum TransactionStatus {
  DELETED,
  PENDING,
  PENDING_REVERTED,
  CONFIRMED,
  SCHEDULED;

  public boolean isPendingReverted() {
    return this == TransactionStatus.PENDING_REVERTED;
  }

  public boolean isDeleted() {
    return this == DELETED;
  }

  public boolean isPending() {
    return this == TransactionStatus.PENDING;
  }

  public boolean isScheduled() {
    return this == SCHEDULED;
  }

  public boolean isConfirmed() {
    return this == CONFIRMED;
  }
}
