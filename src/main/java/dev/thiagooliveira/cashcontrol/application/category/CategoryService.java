package dev.thiagooliveira.cashcontrol.application.category;

import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryService {

  CategorySummary createCategory(CreateCategoryCommand command);

  List<CategorySummary> get(UUID organizationId, UUID accountId);

  Optional<CategorySummary> get(UUID organizationId, UUID accountId, UUID categoryId);
}
