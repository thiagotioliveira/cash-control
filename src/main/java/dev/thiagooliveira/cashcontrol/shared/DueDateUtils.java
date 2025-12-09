package dev.thiagooliveira.cashcontrol.shared;

import java.time.LocalDate;

public class DueDateUtils {

  public static LocalDate nextDueDate(LocalDate dueDate, Recurrence recurrence) {
    return switch (recurrence) {
      case WEEKLY -> dueDate.plusWeeks(1);
      case BIWEEKLY -> dueDate.plusWeeks(2);
      case MONTHLY -> dueDate.plusMonths(1);
      default ->
          throw new IllegalArgumentException(String.format("Invalid recurrence: %s", recurrence));
    };
  }

  public static int totalInstallments(
      LocalDate startDueDate, LocalDate endDate, Recurrence recurrence) {
    if (startDueDate == null || endDate == null || recurrence == null) {
      throw new IllegalArgumentException("Parameters cannot be null");
    }

    if (endDate.isBefore(startDueDate)) {
      throw new IllegalArgumentException("End date must be after start date");
    }

    return switch (recurrence) {
      case WEEKLY -> (int) startDueDate.until(endDate).getDays() / 7 + 1;
      case BIWEEKLY -> (int) (startDueDate.until(endDate).getDays() / 14) + 1;
      case MONTHLY -> (int) startDueDate.until(endDate).toTotalMonths() + 1;
      default -> throw new IllegalArgumentException("Invalid recurrence");
    };
  }
}
