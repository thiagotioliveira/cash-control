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
  public List<CategorySummary> findAllByOrganizationIdAndAccountId(
      UUID organizationId, UUID accountId) {
    return this.repository.findAllByOrganizationIdAndAccountId(organizationId, accountId).stream()
        .map(CategoryEntity::toDomain)
        .toList();
  }

  @Override
  public Optional<CategorySummary> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id) {
    return this.repository
        .findByOrganizationIdAndAccountIdAndId(organizationId, accountId, id)
        .map(CategoryEntity::toDomain);
  }

  @Override
  public Optional<CategorySummary> findByOrganizationIdAndAccountIdAndNameAndType(
      UUID organizationId, UUID accountId, String name, TransactionType type) {
    return this.repository
        .findByOrganizationIdAndAccountIdAndNameAndType(organizationId, accountId, name, type)
        .map(CategoryEntity::toDomain);
  }

  @Override
  public boolean existsByOrganizationIdAndAccountIdAndHashColor(
      UUID organizationId, UUID accountId, String hashColor) {
    return this.repository.existsByOrganizationIdAndAccountIdAndHashColor(
        organizationId, accountId, hashColor);
  }
}
