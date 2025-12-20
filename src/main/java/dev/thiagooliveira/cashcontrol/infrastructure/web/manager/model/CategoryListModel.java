package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.List;
import java.util.UUID;

public class CategoryListModel {

  private final List<CategoryItem> content;

  public CategoryListModel(List<CategorySummary> categories) {
    this.content =
        categories.stream()
            .map(c -> new CategoryItem(c.id(), c.accountId(), c.name(), c.hashColor(), c.type()))
            .toList();
  }

  public List<CategoryItem> getContent() {
    return content;
  }

  public static class CategoryItem {
    private final UUID id;
    private final UUID accountId;
    private final String name;
    private final String hashColor;
    private final TransactionType type;

    public CategoryItem(
        UUID id, UUID accountId, String name, String hashColor, TransactionType type) {
      this.id = id;
      this.accountId = accountId;
      this.name = name;
      this.hashColor = hashColor;
      this.type = type;
    }

    public CategoryActionSheetModel toActionSheetModel() {
      return new CategoryActionSheetModel(
          "Editar", id, accountId, name, hashColor.substring(1), type.toString());
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

    public TransactionType getType() {
      return type;
    }

    public String getTypeFormatted() {
      return type.isCredit() ? "Crédito" : "Debito";
    }
  }
}
