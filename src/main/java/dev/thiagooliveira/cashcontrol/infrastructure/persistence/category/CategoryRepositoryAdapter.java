package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryRepositoryAdapter implements CategoryRepository {

  private final CategoryJpaRepository repository;

  public CategoryRepositoryAdapter(CategoryJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<CategorySummary> findAllByOrganizationId(UUID organizationId) {
    return this.repository.findAllByOrganizationId(organizationId).stream()
        .map(CategoryEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<CategorySummary> findByOrganizationIdAndId(UUID organizationId, UUID id) {
    return this.repository
        .findByOrganizationIdAndId(organizationId, id)
        .map(CategoryEntity::toDomain);
  }

  @Override
  public Optional<CategorySummary> findByOrganizationIdAndNameAndType(
      UUID organizationId, String name, TransactionType type) {
    return this.repository
        .findByOrganizationIdAndNameAndType(organizationId, name, type)
        .map(CategoryEntity::toDomain);
  }
}
