package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  Optional<Category> findById(UUID id);

  Optional<Category> findByTypeAndDefaultCategoryIsTrue(TransactionType type);

  boolean existsByNameAndType(String name, TransactionType type);
}
