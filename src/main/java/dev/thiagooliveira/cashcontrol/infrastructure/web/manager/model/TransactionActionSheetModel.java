package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class TransactionActionSheetModel {

  private static final DateTimeFormatter dtfHourOfDay =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  private final boolean showDetailButton;
  private final boolean showCategoryPicker;
  private final String title;
  private final List<CategoryModel> categories;
  private final UUID id;
  private final Currency currency;
  private final UUID categoryId;
  private final String categoryName;
  private final String description;
  private final int dueDayOfMonth;
  private final String occurredAt;
  private final BigDecimal amount;

  public TransactionActionSheetModel(GetTransactionItem transaction, boolean showDetailButton) {
    this.showDetailButton = showDetailButton;
    this.showCategoryPicker = false;
    this.id = transaction.transactionId();
    this.currency = transaction.currency();
    this.categoryId = transaction.categoryId();
    this.categoryName = transaction.categoryName();
    this.title = this.categoryName;
    this.description = transaction.description();
    this.dueDayOfMonth = transaction.dueDate().getDayOfMonth();
    if (transaction.occurredAt().isPresent()) {
      this.occurredAt =
          dtfHourOfDay.format(transaction.occurredAt().get().atZone(zoneId).toLocalDateTime());
    } else {
      this.occurredAt = null;
    }
    this.amount = transaction.amount();
    this.categories = null;
  }

  public TransactionActionSheetModel(
      String title, Currency currency, List<CategoryModel> categories) {
    this.showDetailButton = false;
    this.showCategoryPicker = true;
    this.title = title;
    this.categories = categories;
    this.id = null;
    this.currency = currency;
    this.categoryId = null;
    this.categoryName = null;
    this.description = null;
    this.dueDayOfMonth = 5;
    this.occurredAt = dtfHourOfDay.format(LocalDateTime.now(zoneId));
    this.amount = BigDecimal.TEN;
  }

  public String getTitle() {
    return title;
  }

  public boolean isShowDetailButton() {
    return showDetailButton;
  }

  public boolean isShowCategoryPicker() {
    return showCategoryPicker;
  }

  public UUID getId() {
    return id;
  }

  public Currency getCurrency() {
    return currency;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public String getCategoryName() {
    return categoryName;
  }

  public String getDescription() {
    return description;
  }

  public int getDueDayOfMonth() {
    return dueDayOfMonth;
  }

  public String getOccurredAt() {
    return occurredAt;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }

  public static class TransactionForm {
    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String symbol;
    private String description;
    private BigDecimal amount;
    private int dueDayOfMonth;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime occurredAt;

    public TransactionForm() {}

    public TransactionForm(GetTransactionItem transaction) {
      this.id = transaction.transactionId();
      this.categoryId = transaction.categoryId();
      this.categoryName = transaction.categoryName();
      this.symbol = transaction.currency().getSymbol();
      this.description = transaction.description();
      this.amount = transaction.amount();
      this.dueDayOfMonth = transaction.dueDate().getDayOfMonth();
      this.occurredAt =
          transaction.occurredAt().isPresent()
              ? LocalDateTime.ofInstant(transaction.occurredAt().get(), zoneId)
              : null;
    }

    public UUID getId() {
      return id;
    }

    public void setId(UUID id) {
      this.id = id;
    }

    public UUID getCategoryId() {
      return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
      this.categoryId = categoryId;
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

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public int getDueDayOfMonth() {
      return dueDayOfMonth;
    }

    public void setDueDayOfMonth(int dueDayOfMonth) {
      this.dueDayOfMonth = dueDayOfMonth;
    }

    public LocalDateTime getOccurredAt() {
      return occurredAt;
    }

    public String getOccurredAtFormatted() {
      return occurredAt != null ? FormattersUtils.dtfHourOfDay.format(occurredAt) : "-";
    }

    public void setOccurredAt(LocalDateTime occurredAt) {
      this.occurredAt = occurredAt;
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

    public void setAmount(BigDecimal amount) {
      this.amount = amount;
    }
  }

  public static class CategoryModel {

    private final UUID id;
    private final String name;
    private final String hashColor;
    private final String type;

    public CategoryModel(Category category) {
      this.id = category.getId();
      this.name = category.getName();
      this.hashColor = category.getHashColor();
      this.type = category.getType().toString();
    }

    public UUID getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getHashColor() {
      return hashColor;
    }

    public String getType() {
      return type;
    }
  }

  public static class ListCategoryModel {

    private final List<CategoryModel> credit;
    private final List<CategoryModel> debit;

    public ListCategoryModel(List<Category> categories) {
      this.credit =
          categories.stream().filter(c -> c.getType().isCredit()).toList().stream()
              .map(CategoryModel::new)
              .toList();
      this.debit =
          categories.stream().filter(c -> c.getType().isDebit()).toList().stream()
              .map(CategoryModel::new)
              .toList();
    }

    public List<CategoryModel> getCredit() {
      return credit;
    }

    public List<CategoryModel> getDebit() {
      return debit;
    }
  }
}
