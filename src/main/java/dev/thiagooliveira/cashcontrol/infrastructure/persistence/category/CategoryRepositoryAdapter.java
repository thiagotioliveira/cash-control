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
  public Optional<Category> findById(UUID id) {
    return this.repository.findById(id).map(CategoryEntity::toDomain);
  }

  @Override
  public Optional<Category> findByTypeAndDefaultCategoryIsTrue(TransactionType type) {
    return this.repository.findByTypeAndDefaultCategoryIsTrue(type).map(CategoryEntity::toDomain);
  }

  @Override
  public boolean existsByNameAndType(String name, TransactionType type) {
    return this.repository.existsByNameAndType(name, type);
  }
}
