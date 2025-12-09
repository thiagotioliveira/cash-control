package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.domain.bank.Bank;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionsModel {

  private final List<TransactionItem> content = new ArrayList<>();

  public TransactionsModel(
      DecimalFormat df,
      DateTimeFormatter dtf,
      DateTimeFormatter dtfHourOfDay,
      Bank bank,
      List<GetTransactionItem> transactions) {
    transactions.forEach(
        t ->
            content.add(
                new TransactionItem(t, df, dtf, dtfHourOfDay, bank.getCurrency().getSymbol())));
  }

  public List<TransactionItem> getContent() {
    return content;
  }

  public static class TransactionItem {
    private final UUID id;
    private final UUID transactionTemplateId;
    private final String occurredAt;
    private final String dueDate;
    private final String description;
    private final String amount;
    private final UUID categoryId;
    private final String categoryName;
    private final String categoryHashColor;
    private final TransactionType type;
    private final TransactionStatus status;

    public TransactionItem(
        GetTransactionItem transaction,
        DecimalFormat df,
        DateTimeFormatter dtf,
        DateTimeFormatter dtfHourOfDay,
        String symbol) {
      this.id = transaction.transactionId();
      this.transactionTemplateId = transaction.transactionTemplateId().orElse(null);
      this.occurredAt =
          transaction.occurredAt().isPresent()
              ? LocalDateTime.ofInstant(transaction.occurredAt().get(), ZoneId.systemDefault())
                  .format(dtfHourOfDay)
              : "";
      this.dueDate = transaction.dueDate().format(dtf);
      this.description = transaction.description();
      this.categoryId = transaction.categoryId();
      this.categoryName = transaction.categoryName();
      this.categoryHashColor = transaction.categoryHashColor();
      this.type = transaction.type();
      this.status = transaction.status();
      this.amount =
          this.type.isCredit()
              ? "+ " + symbol + " " + df.format(transaction.amount())
              : "- " + symbol + " " + df.format(transaction.amount());
    }

    public UUID getId() {
      return id;
    }

    public UUID getTransactionTemplateId() {
      return transactionTemplateId;
    }

    public String getOccurredAt() {
      return occurredAt;
    }

    public String getDueDate() {
      return dueDate;
    }

    public String getDescription() {
      return description;
    }

    public String getAmount() {
      return amount;
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
}
