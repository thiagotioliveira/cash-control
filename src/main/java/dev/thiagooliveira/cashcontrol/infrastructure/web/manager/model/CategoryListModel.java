package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.application.category.dto.GetCategoryItem;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.List;
import java.util.UUID;

public class CategoryListModel {

  private final List<CategoryItem> content;

  public CategoryListModel(List<GetCategoryItem> categories) {
    this.content =
        categories.stream()
            .map(c -> new CategoryItem(c.id(), c.name(), c.hashColor(), c.type()))
            .toList();
  }

  public List<CategoryItem> getContent() {
    return content;
  }

  public static class CategoryItem {
    private final UUID id;
    private final String name;
    private final String hashColor;
    private final TransactionType type;

    public CategoryItem(UUID id, String name, String hashColor, TransactionType type) {
      this.id = id;
      this.name = name;
      this.hashColor = hashColor;
      this.type = type;
    }

    public CategoryActionSheetModel toActionSheetModel() {
      return new CategoryActionSheetModel(
          "Editar", id, name, hashColor.substring(1), type.toString());
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
