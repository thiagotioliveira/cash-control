package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.domain.category.Category;
import java.util.List;
import java.util.UUID;

public class ListCategoryModel {

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
}
