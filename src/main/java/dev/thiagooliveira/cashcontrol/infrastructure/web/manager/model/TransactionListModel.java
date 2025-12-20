package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;
import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.df;

import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TransactionListModel {

  private final UUID accountId;
  private final Map<LocalDate, List<TransactionItem>> content =
      new TreeMap<>(Comparator.reverseOrder());

  public TransactionListModel(UUID accountId, List<TransactionSummary> transactions) {
    this.accountId = accountId;
    transactions.forEach(
        t -> {
          LocalDate key =
              t.occurredAt().map(date -> date.atZone(zoneId).toLocalDate()).orElse(t.dueDate());
          content.computeIfAbsent(key, k -> new ArrayList<>()).add(new TransactionItem(t));
        });
    this.content
        .values()
        .forEach(
            list ->
                list.sort(
                    (t1, t2) -> {
                      if (t1.getOccurredAt() != null && t2.getOccurredAt() != null) {
                        return t2.getOccurredAt().compareTo(t1.getOccurredAt());
                      }
                      return t2.getDueDate().compareTo(t1.getDueDate());
                    }));
  }

  public String keyFormatted(LocalDate key) {
    var today = LocalDate.now(zoneId);
    if (today.equals(key)) {
      return "Hoje";
    } else if (today.minusDays(1).equals(key)) {
      return "Ontem";
    }
    return dtf.format(key);
  }

  public UUID getAccountId() {
    return accountId;
  }

  public Map<LocalDate, List<TransactionItem>> getContent() {
    return content;
  }

  public class TransactionItem {
    private final UUID id;
    private final UUID accountId;
    private final UUID transactionTemplateId;
    private final Instant occurredAt;
    private final String occurredAtFormatted;
    private final LocalDate dueDate;
    private final int dueDayOfMonth;
    private final String dueDateFormatted;
    private final String description;
    private final dev.thiagooliveira.cashcontrol.shared.Currency currency;
    private final BigDecimal amount;
    private final String balanceFormatted;
    private final String amountFormatted;
    private final UUID categoryId;
    private final String categoryName;
    private final String categoryHashColor;
    private final TransactionType type;
    private final TransactionStatus status;

    public TransactionItem(TransactionSummary transaction) {
      this.id = transaction.transactionId();
      this.accountId = transaction.accountId();
      this.transactionTemplateId = transaction.transactionTemplateId().orElse(null);
      this.occurredAt = transaction.occurredAt().orElse(null);
      this.occurredAtFormatted =
          transaction.occurredAt().isPresent()
              ? LocalDateTime.ofInstant(transaction.occurredAt().get(), zoneId).format(dtfHourOfDay)
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
      this.balanceFormatted =
          transaction.accountBalance().isPresent()
              ? this.currency.getSymbol() + " " + df.format(transaction.accountBalance().get())
              : "";
    }

    public int getDueDayOfMonth() {
      return dueDayOfMonth;
    }

    public UUID getId() {
      return id;
    }

    public UUID getAccountId() {
      return accountId;
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

    public String getBalanceFormatted() {
      return balanceFormatted;
    }

    public String getStatusFormatted() {
      return this.status.isScheduled() ? "Agendado" : this.status.isConfirmed() ? "Confirmado" : "";
    }
  }
}
