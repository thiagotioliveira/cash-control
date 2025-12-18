package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.category.CategoryServiceImpl;
import dev.thiagooliveira.cashcontrol.application.category.CreateCategory;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.category.CategoryEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.category.CategoryRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.transactional.category.CategoryServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryConfig {

  @Bean
  CategoryEventListener categoryEventListener(CategoryJpaRepository repository) {
    return new CategoryEventListener(repository);
  }

  @Bean
  CategoryService categoryService(
      CategoryJpaRepository repository, EventStore eventStore, EventPublisher publisher) {
    var categoryRepository = categoryRepository(repository);
    return new CategoryServiceProxy(
        new CategoryServiceImpl(
            categoryRepository, createCategory(categoryRepository, eventStore, publisher)));
  }

  private CategoryRepository categoryRepository(CategoryJpaRepository repository) {
    return new CategoryRepositoryAdapter(repository);
  }

  private CreateCategory createCategory(
      CategoryRepository repository, EventStore eventStore, EventPublisher publisher) {
    return new CreateCategory(repository, eventStore, publisher);
  }
}
