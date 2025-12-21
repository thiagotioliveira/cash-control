package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.shared.FormattersUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;

public class TransferActionSheetModel {
  private UUID accountIdFrom;
  private List<AccountModel> accountsTo;
  private List<CategoryModel> categoriesFrom;
  private UUID categoryIdFrom;
  private UUID categoryIdTo;
  private UUID accountIdTo;
  private String description;
  private String occurredAt;
  private Map<UUID, List<CategoryModel>> categoriesTo;
  private BigDecimal amountFrom = BigDecimal.TEN;
  private BigDecimal amountTo = BigDecimal.TEN;
  private String symbolFrom = "€";
  private String symbolTo = "€";

  public TransferActionSheetModel(
      UUID accountIdFrom,
      List<CategoryModel> categoriesFrom,
      List<AccountModel> accountsTo,
      Map<UUID, List<CategoryModel>> categoriesTo) {
    this.occurredAt = dtfHourOfDay.format(LocalDateTime.now(zoneId));
    this.accountIdFrom = accountIdFrom;
    this.categoriesFrom = categoriesFrom;
    this.accountsTo = accountsTo;
    this.categoriesTo = categoriesTo;
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

  public UUID getCategoryIdFrom() {
    return categoryIdFrom;
  }

  public void setCategoryIdFrom(UUID categoryIdFrom) {
    this.categoryIdFrom = categoryIdFrom;
  }

  public UUID getCategoryIdTo() {
    return categoryIdTo;
  }

  public void setCategoryIdTo(UUID categoryIdTo) {
    this.categoryIdTo = categoryIdTo;
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

  public List<CategoryModel> getCategoriesFrom() {
    return categoriesFrom;
  }

  public Map<UUID, List<CategoryModel>> getCategoriesTo() {
    return categoriesTo;
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
    private UUID categoryIdFrom;
    private String categoryNameFrom;
    private UUID categoryIdTo;
    private String categoryNameTo;
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

    public UUID getCategoryIdFrom() {
      return categoryIdFrom;
    }

    public void setCategoryIdFrom(UUID categoryIdFrom) {
      this.categoryIdFrom = categoryIdFrom;
    }

    public String getCategoryNameFrom() {
      return categoryNameFrom;
    }

    public void setCategoryNameFrom(String categoryNameFrom) {
      this.categoryNameFrom = categoryNameFrom;
    }

    public UUID getCategoryIdTo() {
      return categoryIdTo;
    }

    public void setCategoryIdTo(UUID categoryIdTo) {
      this.categoryIdTo = categoryIdTo;
    }

    public String getCategoryNameTo() {
      return categoryNameTo;
    }

    public void setCategoryNameTo(String categoryNameTo) {
      this.categoryNameTo = categoryNameTo;
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
