package dev.thiagooliveira.cashcontrol.infrastructure.listener.category;

import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.CategoryCreated;
import dev.thiagooliveira.cashcontrol.domain.event.transaction.v1.CategoryUpdated;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryJpaRepository;
import java.util.UUID;
import org.springframework.context.event.EventListener;

public class CategoryEventListener {

  private final CategoryJpaRepository repository;

  public CategoryEventListener(CategoryJpaRepository repository) {
    this.repository = repository;
  }

  @EventListener
  public void on(CategoryCreated event) {
    var entity = new CategoryEntity(event);
    repository.save(entity);
  }

  @EventListener
  public void on(CategoryUpdated event) {
    var entity = findById(event.categoryId());
    entity.update(event);
    this.repository.save(entity);
  }

  private CategoryEntity findById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> InfrastructureException.notFound("category not found"));
  }
}
