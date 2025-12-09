package dev.thiagooliveira.cashcontrol.shared;

public enum Recurrence {
  NONE,
  WEEKLY,
  BIWEEKLY,
  MONTHLY;

  public boolean isNone() {
    return this == NONE;
  }
}
