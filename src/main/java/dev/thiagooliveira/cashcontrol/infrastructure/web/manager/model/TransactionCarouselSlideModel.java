package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.df;
import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.dtf;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionCarouselSlideModel {

  private final List<TransactionCarouselSlideItem> items = new ArrayList<>();

  public TransactionCarouselSlideModel(List<GetTransactionItem> transactions) {
    transactions.forEach(t -> items.add(new TransactionCarouselSlideItem(t)));
  }

  public List<TransactionCarouselSlideItem> getItems() {
    return items;
  }

  public static class TransactionCarouselSlideItem {
    private final UUID id;
    private final Currency currency;
    private final TransactionType type;
    private final BigDecimal amount;
    private final String categoryName;
    private final LocalDate dueDate;
    private final GetTransactionItem transaction;

    public TransactionCarouselSlideItem(GetTransactionItem transaction) {
      this.transaction = transaction;
      this.id = transaction.transactionId();
      this.currency = transaction.currency();
      this.type = transaction.type();
      this.amount = transaction.amount();
      this.categoryName = transaction.categoryName();
      this.dueDate = transaction.dueDate();
    }

    public TransactionActionSheetModel toActionSheetModel() {
      return new TransactionActionSheetModel(this.transaction, true);
    }

    public UUID getId() {
      return id;
    }

    public TransactionType getType() {
      return type;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public String getAmountFormatted() {
      return this.type.isCredit()
          ? "+ " + this.currency.getSymbol() + " " + df.format(this.amount)
          : "- " + this.currency.getSymbol() + " " + df.format(this.amount);
    }

    public String getCategoryName() {
      return categoryName;
    }

    public LocalDate getDueDate() {
      return dueDate;
    }

    public String getDueDateFormatted() {
      return dueDate.format(dtf);
    }
  }
}
