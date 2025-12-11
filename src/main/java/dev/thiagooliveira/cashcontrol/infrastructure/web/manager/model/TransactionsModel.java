package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class TransactionsModel {

  private final List<TransactionItem> content = new ArrayList<>();

  public TransactionsModel(List<GetTransactionItem> transactions) {
    transactions.forEach(t -> content.add(new TransactionItem(t)));
  }

  public List<TransactionItem> getContent() {
    return content;
  }

  public static class UpdateTransactionModel {
    private UUID id;
    private String symbol;
    private String categoryName;
    private String description;
    private BigDecimal amount;

    private int dueDayOfMonth;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime occurredAt;

    public UpdateTransactionModel() {}

    public UpdateTransactionModel(GetTransactionItem transaction) {
      this.id = transaction.transactionId();
      this.symbol = transaction.currency().getSymbol();
      this.categoryName = transaction.categoryName();
      this.description = transaction.description();
      this.amount = transaction.amount();
      this.dueDayOfMonth = transaction.dueDate().getDayOfMonth();
      this.occurredAt =
          transaction.occurredAt().isPresent()
              ? LocalDateTime.ofInstant(transaction.occurredAt().get(), FormattersUtils.zoneId)
              : null;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public void setCategoryName(String categoryName) {
      this.categoryName = categoryName;
    }

    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }

    public String getDescription() {
      return description;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public String getAmountFormatted() {
      if (amount != null) {
        return symbol + " " + df.format(amount);
      }
      return "";
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }

    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }

    public String getOccurredAtFormatted() {
      return occurredAt != null ? dtfHourOfDay.format(occurredAt) : "-";
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
      this.occurredAt = occurredAt;
    }

    public int getDueDayOfMonth() {
      return dueDayOfMonth;
    }

    public void setDueDayOfMonth(int dueDayOfMonth) {
      this.dueDayOfMonth = dueDayOfMonth;
    }
  }
}
