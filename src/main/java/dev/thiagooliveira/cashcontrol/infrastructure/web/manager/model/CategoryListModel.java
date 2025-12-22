package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import dev.thiagooliveira.cashcontrol.shared.CategoryType;
import java.util.List;
import java.util.UUID;

public class CategoryListModel {

  private final List<CategoryItem> content;

  public CategoryListModel(List<CategorySummary> categories) {
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
    private final CategoryType type;

    public CategoryItem(UUID id, String name, String hashColor, CategoryType type) {
      this.id = id;
      this.name = name;
      this.hashColor = hashColor;
      this.type = type;
    }

    public CategoryActionSheetModel toActionSheetModel() {
      return new CategoryActionSheetModel("Editar", id, name, hashColor, type.toString());
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

    public CategoryType getType() {
      return type;
    }

    public String getTypeFormatted() {
      return type.isCredit() ? "Crédito" : type.isDebit() ? "Debito" : "Tranferência";
    }
  }
}
