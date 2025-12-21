package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransactionDetailsModel {
  private final UUID id;
  private final UUID accountId;
  private final String accountName;
  private final String title;
  private final String backLink;
  private final String description;
  private final Currency currency;
  private final TransactionStatus status;
  private final String categoryName;
  private final String userName;
  private final LocalDate dueDate;
  private final LocalDateTime occurredAt;
  private final BigDecimal amount;
  private final TransactionSummary transaction;
  private final Recurrence recurrence;
  private final Integer installments;

  public TransactionDetailsModel(TransactionSummary transaction, String backLink) {
    this.transaction = transaction;
    this.title =
        "Detalhe d"
            + ((transaction.categoryType().isCredit())
                ? "o Crédito"
                : (transaction.categoryType().isDebit() ? "o Debito" : "a Transferência"));
    this.backLink = backLink;
    this.id = transaction.transactionId();
    this.accountName = transaction.accountName();
    this.accountId = transaction.accountId();
    this.currency = transaction.currency();
    this.description = transaction.description();
    this.status = transaction.status();
    this.categoryName = transaction.categoryName();
    this.userName = transaction.userName().orElse(null);
    this.dueDate = transaction.dueDate();
    this.amount = transaction.amount();
    this.recurrence = transaction.recurrence().orElse(null);
    this.installments = transaction.installments().orElse(null);
    this.occurredAt =
        transaction.occurredAt().isPresent()
            ? LocalDateTime.ofInstant(transaction.occurredAt().get(), zoneId)
            : null;
  }

  public TransactionActionSheetModel toActionSheetModel() {
    return new TransactionActionSheetModel(this.transaction, false);
  }

  public String getTitle() {
    return title;
  }

  public String getBackLink() {
    return backLink;
  }

  public UUID getId() {
    return id;
  }

  public String getAccountName() {
    return accountName;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public String getDescription() {
    return description;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public String getStatusFormatted() {
    return this.status.isScheduled() ? "Agendado" : this.status.isConfirmed() ? "Confirmado" : "";
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getUserName() {
    return userName != null ? userName : "-";
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public String getDueDateFormatted() {
    return dueDate.format(dtf);
  }

  public LocalDateTime getOccurredAt() {
    return occurredAt;
  }

  public String getOccurredAtFormatted() {
    return occurredAt != null ? dtfHourOfDay.format(occurredAt) : "-";
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getAmountFormatted() {
    if (amount != null) {
      return this.currency.getSymbol() + " " + df.format(amount);
    }
    return "";
  }

  public String getRecurrenceFormatted() {
    if (recurrence == null) return "-";
    switch (recurrence) {
      case NONE:
        return "Nunca";
      case WEEKLY:
        return "Semanalmente";
      case BIWEEKLY:
        return "Bimestralmente";
      case MONTHLY:
        return "Mensalmente";
      default:
        return "-";
    }
  }

  public Integer getInstallments() {
    return installments;
  }

  public String getInstallmentsFormatted() {
    return installments != null ? installments.toString() : "-";
  }
}
