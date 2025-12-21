package dev.thiagooliveira.cashcontrol.domain.category;

import dev.thiagooliveira.cashcontrol.shared.CategoryType;
import java.util.UUID;

public record CategorySummary(UUID id, String name, String hashColor, CategoryType type) {

  public CategorySummary(Category category) {
    this(category.getId(), category.getName(), category.getHashColor(), category.getType());
  }
}
