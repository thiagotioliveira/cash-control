package dev.thiagooliveira.cashcontrol.shared;

public enum Recurrence {
  NONE,
  BIWEEKLY,
  MONTHLY;

  public boolean isNone() {
    return this == NONE;
  }
}
