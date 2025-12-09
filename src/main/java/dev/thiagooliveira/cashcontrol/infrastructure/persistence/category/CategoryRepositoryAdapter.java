package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.Optional;
import java.util.UUID;

public class CategoryRepositoryAdapter implements CategoryRepository {

  private final CategoryJpaRepository repository;

  public CategoryRepositoryAdapter(CategoryJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Category> findByOrganizationIdAndId(UUID organizationId, UUID id) {
    return this.repository
        .findByOrganizationIdAndId(organizationId, id)
        .map(CategoryEntity::toDomain);
  }

  @Override
  public Optional<Category> findByOrganizationIdAndNameAndType(
      UUID organizationId, String name, TransactionType type) {
    return this.repository
        .findByOrganizationIdAndNameAndType(organizationId, name, type)
        .map(CategoryEntity::toDomain);
  }
}
