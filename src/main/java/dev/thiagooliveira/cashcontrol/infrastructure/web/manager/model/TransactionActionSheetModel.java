package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.category.dto.GetCategoryItem;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.util.Strings;
import org.springframework.format.annotation.DateTimeFormat;

public class TransactionActionSheetModel {

  private static final DateTimeFormatter dtfHourOfDay =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

  private final boolean showDetailButton;
  private final boolean showStartDueDateInput;
  private final boolean showOccurredAtInput;
  private final boolean showCategoryPicker;
  private final boolean showRecurrenceInput;
  private final boolean showInstallmentInput;
  private final String title;
  private final List<CategoryModel> categories;
  private final UUID id;
  private final Currency currency;
  private final UUID categoryId;
  private final String categoryName;
  private final String description;
  private final LocalDate startDueDate;
  private final String occurredAt;
  private final BigDecimal amount;
  private final String recurrence;
  private final Integer installments;

  public TransactionActionSheetModel(GetTransactionItem transaction, boolean showDetailButton) {
    this.showDetailButton = showDetailButton;
    this.showStartDueDateInput = true;
    this.showOccurredAtInput = true;
    this.showCategoryPicker = false;
    this.showRecurrenceInput = false;
    this.showInstallmentInput = false;
    this.id = transaction.transactionId();
    this.currency = transaction.currency();
    this.categoryId = transaction.categoryId();
    this.categoryName = transaction.categoryName();
    this.title = this.categoryName;
    this.description = transaction.description();
    this.startDueDate = transaction.dueDate();
    if (transaction.occurredAt().isPresent()) {
      this.occurredAt =
          dtfHourOfDay.format(transaction.occurredAt().get().atZone(zoneId).toLocalDateTime());
    } else {
      this.occurredAt = null;
    }
    this.amount = transaction.amount();
    this.categories = null;
    this.recurrence = transaction.recurrence().map(Enum::name).orElse(null);
    this.installments = transaction.installments().orElse(null);
  }

  public TransactionActionSheetModel(
      String title,
      Currency currency,
      List<CategoryModel> categories,
      boolean showStartDueDateInput,
      boolean showOccurredAtInput,
      boolean showRecurrenceInput,
      boolean showInstallmentInput) {
    this.showDetailButton = false;
    this.showCategoryPicker = true;
    this.showRecurrenceInput = showRecurrenceInput;
    this.showInstallmentInput = showInstallmentInput;
    this.showOccurredAtInput = showOccurredAtInput;
    this.showStartDueDateInput = showStartDueDateInput;
    this.title = title;
    this.categories = categories;
    this.id = null;
    this.currency = currency;
    this.categoryId = null;
    this.categoryName = null;
    this.description = null;
    this.startDueDate = null;
    this.occurredAt = dtfHourOfDay.format(LocalDateTime.now(zoneId));
    this.amount = BigDecimal.TEN;
    if (showRecurrenceInput) {
      this.recurrence = Recurrence.NONE.name();
    } else {
      this.recurrence = null;
    }
    this.installments = null;
  }

  public String getRecurrence() {
    return recurrence;
  }

  public Integer getInstallments() {
    return installments;
  }

  public String getTitle() {
    return title;
  }

  public boolean isShowDetailButton() {
    return showDetailButton;
  }

  public boolean isShowOccurredAtInput() {
    return showOccurredAtInput;
  }

  public boolean isShowCategoryPicker() {
    return showCategoryPicker;
  }

  public boolean isShowRecurrenceInput() {
    return showRecurrenceInput;
  }

  public boolean isShowInstallmentInput() {
    return showInstallmentInput;
  }

  public boolean isShowStartDueDateInput() {
    return showStartDueDateInput;
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

  public LocalDate getStartDueDate() {
    return startDueDate;
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
    private String recurrence;
    private Integer installments;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDueDate;

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
      this.startDueDate = transaction.dueDate();
      this.occurredAt =
          transaction.occurredAt().isPresent()
              ? LocalDateTime.ofInstant(transaction.occurredAt().get(), zoneId)
              : null;
      this.recurrence = transaction.recurrence().map(Enum::name).orElse(null);
      this.installments = transaction.installments().orElse(null);
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

    public LocalDate getStartDueDate() {
      return startDueDate;
    }

    public String getStartDueDateFormatted() {
      return startDueDate != null ? FormattersUtils.dtf.format(startDueDate) : "-";
    }

    public void setStartDueDate(LocalDate startDueDate) {
      this.startDueDate = startDueDate;
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

    public String getRecurrence() {
      return recurrence;
    }

    public String getRecurrenceFormatted() {
      if (Strings.isBlank(recurrence)) return "-";
      var r = Recurrence.valueOf(recurrence);
      switch (r) {
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

    public void setRecurrence(String recurrence) {
      this.recurrence = recurrence;
    }

    public Integer getInstallments() {
      return installments;
    }

    public String getInstallmentsFormatted() {
      return installments != null ? installments.toString() : "-";
    }

    public void setInstallments(Integer installments) {
      this.installments = installments;
    }
  }

  public static class CategoryModel {

    private final UUID id;
    private final String name;
    private final String hashColor;
    private final String type;

    public CategoryModel(GetCategoryItem category) {
      this.id = category.id();
      this.name = category.name();
      this.hashColor = category.hashColor();
      this.type = category.type().toString();
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

    public ListCategoryModel(List<GetCategoryItem> categories) {
      this.credit =
          categories.stream().filter(c -> c.type().isCredit()).toList().stream()
              .map(CategoryModel::new)
              .toList();
      this.debit =
          categories.stream().filter(c -> c.type().isDebit()).toList().stream()
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
