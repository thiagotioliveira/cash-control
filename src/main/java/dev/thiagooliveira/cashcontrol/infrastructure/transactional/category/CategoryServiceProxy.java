package dev.thiagooliveira.cashcontrol.infrastructure.transactional.category;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.category.dto.UpdateCategoryCommand;
import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CategoryServiceProxy implements CategoryService {

  private final CategoryService categoryService;

  public CategoryServiceProxy(CategoryService categoryService) {
    this.categoryService = categoryService;
  }

  @Override
  public CategorySummary createCategory(CreateCategoryCommand command) {
    return this.categoryService.createCategory(command);
  }

  @Override
  public List<CategorySummary> get(UUID organizationId) {
    return this.categoryService.get(organizationId);
  }

  @Override
  public Optional<CategorySummary> get(UUID organizationId, UUID categoryId) {
    return this.categoryService.get(organizationId, categoryId);
  }

  @Override
  public CategorySummary update(UpdateCategoryCommand command) {
    return this.categoryService.update(command);
  }
}
