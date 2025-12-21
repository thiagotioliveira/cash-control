package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.shared.FormattersUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class TransferActionSheetModel {
  private UUID accountIdFrom;
  private List<AccountModel> accountsTo;
  private List<CategoryModel> categories;
  private UUID categoryId;
  private UUID accountIdTo;
  private String description;
  private String occurredAt;
  private BigDecimal amountFrom = BigDecimal.TEN;
  private BigDecimal amountTo = BigDecimal.TEN;
  private String symbolFrom = "€";
  private String symbolTo = "€";

  public TransferActionSheetModel(
      UUID accountIdFrom, List<CategoryModel> categories, List<AccountModel> accountsTo) {
    this.occurredAt = dtfHourOfDay.format(LocalDateTime.now(zoneId));
    this.accountIdFrom = accountIdFrom;
    this.categories = categories;
    this.accountsTo = accountsTo;
  }

  public void setSymbolTo(String symbolTo) {
    this.symbolTo = symbolTo;
  }

  public void setSymbolFrom(String symbolFrom) {
    this.symbolFrom = symbolFrom;
  }

  public String getOccurredAt() {
    return occurredAt;
  }

  public void setOccurredAt(String occurredAt) {
    this.occurredAt = occurredAt;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public UUID getAccountIdTo() {
    return accountIdTo;
  }

  public void setAccountIdTo(UUID accountIdTo) {
    this.accountIdTo = accountIdTo;
  }

  public void setAccountIdFrom(UUID accountIdFrom) {
    this.accountIdFrom = accountIdFrom;
  }

  public UUID getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(UUID categoryId) {
    this.categoryId = categoryId;
  }

  public List<CategoryModel> getCategories() {
    return categories;
  }

  public void setCategories(List<CategoryModel> categories) {
    this.categories = categories;
  }

  public String getSymbolFrom() {
    return symbolFrom;
  }

  public String getSymbolTo() {
    return symbolTo;
  }

  public UUID getAccountIdFrom() {
    return accountIdFrom;
  }

  public List<AccountModel> getAccountsTo() {
    return accountsTo;
  }

  public BigDecimal getAmountFrom() {
    return amountFrom;
  }

  public BigDecimal getAmountTo() {
    return amountTo;
  }

  public static class TransferForm {
    private UUID accountIdFrom;
    private String symbolFrom = "€";
    private String symbolTo = "€";
    private UUID categoryId;
    private String categoryName;
    private UUID accountIdTo;
    private String accountNameFrom;
    private String accountNameTo;
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime occurredAt;

    private BigDecimal amountFrom;
    private BigDecimal amountTo;

    public TransferForm() {}

    public String getAccountNameFrom() {
      return accountNameFrom;
    }

    public void setAccountNameFrom(String accountNameFrom) {
      this.accountNameFrom = accountNameFrom;
    }

    public UUID getAccountIdFrom() {
      return accountIdFrom;
    }

    public void setAccountIdFrom(UUID accountIdFrom) {
      this.accountIdFrom = accountIdFrom;
    }

    public UUID getAccountIdTo() {
      return accountIdTo;
    }

    public void setAccountIdTo(UUID accountIdTo) {
      this.accountIdTo = accountIdTo;
    }

    public String getAccountNameTo() {
      return accountNameTo;
    }

    public void setAccountNameTo(String accountNameTo) {
      this.accountNameTo = accountNameTo;
    }

    public void setSymbolFrom(String symbolFrom) {
      this.symbolFrom = symbolFrom;
    }

    public void setSymbolTo(String symbolTo) {
      this.symbolTo = symbolTo;
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

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
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

    public BigDecimal getAmountFrom() {
      return amountFrom;
    }

    public String getAmountFromFormatted() {
      if (amountFrom != null) {
        return symbolFrom + " " + df.format(amountFrom);
      }
      return "";
    }

    public void setAmountFrom(BigDecimal amountFrom) {
      this.amountFrom = amountFrom;
    }

    public BigDecimal getAmountTo() {
      return amountTo;
    }

    public String getAmountToFormatted() {
      if (amountTo != null) {
        return symbolTo + " " + df.format(amountTo);
      }
      return "";
    }

    public void setAmountTo(BigDecimal amountTo) {
      this.amountTo = amountTo;
    }

    public String getSymbolFrom() {
      return symbolFrom;
    }

    public String getSymbolTo() {
      return symbolTo;
    }
  }
}
