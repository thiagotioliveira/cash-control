package dev.thiagooliveira.cashcontrol.application.category;

import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryServiceImpl implements CategoryService {

  private final CategoryRepository repository;
  private final CreateCategory createCategory;

  public CategoryServiceImpl(CategoryRepository repository, CreateCategory createCategory) {
    this.repository = repository;
    this.createCategory = createCategory;
  }

  @Override
  public CategorySummary createCategory(CreateCategoryCommand command) {
    return this.createCategory.execute(command);
  }

  @Override
  public List<CategorySummary> get(UUID organizationId, UUID accountId) {
    return this.repository.findAllByOrganizationIdAndAccountId(organizationId, accountId);
  }

  @Override
  public Optional<CategorySummary> get(UUID organizationId, UUID accountId, UUID categoryId) {
    return this.repository.findByOrganizationIdAndAccountIdAndId(
        organizationId, accountId, categoryId);
  }
}
