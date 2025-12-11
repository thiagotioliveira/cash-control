package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;
import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.df;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class TransactionItem {
  private final UUID id;
  private final UUID transactionTemplateId;
  private final Instant occurredAt;
  private final String occurredAtFormatted;
  private final LocalDate dueDate;
  private final int dueDayOfMonth;
  private final String dueDateFormatted;
  private final String description;
  private final Currency currency;
  private final BigDecimal amount;
  private final String amountFormatted;
  private final UUID categoryId;
  private final String categoryName;
  private final String categoryHashColor;
  private final TransactionType type;
  private final TransactionStatus status;

  public TransactionItem(GetTransactionItem transaction) {
    this.id = transaction.transactionId();
    this.transactionTemplateId = transaction.transactionTemplateId().orElse(null);
    this.occurredAt = transaction.occurredAt().orElse(null);
    this.occurredAtFormatted =
        transaction.occurredAt().isPresent()
            ? LocalDateTime.ofInstant(transaction.occurredAt().get(), ZoneId.systemDefault())
                .format(dtfHourOfDay)
            : "";
    this.dueDate = transaction.dueDate();
    this.dueDayOfMonth = this.dueDate.getDayOfMonth();
    this.dueDateFormatted = this.dueDate.format(dtf);
    this.description = transaction.description();
    this.categoryId = transaction.categoryId();
    this.categoryName = transaction.categoryName();
    this.categoryHashColor = transaction.categoryHashColor();
    this.type = transaction.type();
    this.status = transaction.status();
    this.amount = transaction.amount();
    this.currency = transaction.currency();
    this.amountFormatted =
        this.type.isCredit()
            ? "+ " + this.currency.getSymbol() + " " + df.format(transaction.amount())
            : "- " + this.currency.getSymbol() + " " + df.format(transaction.amount());
  }

  public int getDueDayOfMonth() {
    return dueDayOfMonth;
  }

  public UUID getId() {
    return id;
  }

  public Currency getCurrency() {
    return currency;
  }

  public UUID getTransactionTemplateId() {
    return transactionTemplateId;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }

  public String getOccurredAtFormatted() {
    return occurredAtFormatted;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public String getDueDateFormatted() {
    return dueDateFormatted;
  }

  public String getDescription() {
    return description;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getAmountFormatted() {
    return amountFormatted;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getCategoryHashColor() {
    return categoryHashColor;
  }

  public TransactionType getType() {
    return type;
  }

  public TransactionStatus getStatus() {
    return status;
  }

  public String getStatusFormatted() {
    return this.status.isScheduled() ? "Agendado" : this.status.isConfirmed() ? "Confirmado" : "";
  }
}
