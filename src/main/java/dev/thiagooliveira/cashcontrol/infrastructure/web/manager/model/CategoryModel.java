package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import java.util.UUID;

public class CategoryModel {

  private final UUID id;
  private final String name;
  private final String hashColor;
  private final String type;

  public CategoryModel(CategorySummary category) {
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
