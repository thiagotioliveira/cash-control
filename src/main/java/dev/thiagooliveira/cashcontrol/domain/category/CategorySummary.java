package dev.thiagooliveira.cashcontrol.domain.category;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.UUID;

public record CategorySummary(
    UUID id, UUID accountId, String name, String hashColor, TransactionType type) {

  public CategorySummary(Category category) {
    this(
        category.getId(),
        category.getAccountId(),
        category.getName(),
        category.getHashColor(),
        category.getType());
  }
}
