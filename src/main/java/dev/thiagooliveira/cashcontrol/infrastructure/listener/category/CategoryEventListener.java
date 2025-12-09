package dev.thiagooliveira.cashcontrol.infrastructure.listener.category;

import dev.thiagooliveira.cashcontrol.domain.event.category.CategoryCreated;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryJpaRepository;
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
}
