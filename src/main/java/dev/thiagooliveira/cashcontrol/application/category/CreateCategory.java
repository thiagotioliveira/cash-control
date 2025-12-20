package dev.thiagooliveira.cashcontrol.application.category;

import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;

public class CreateCategory {

  private final CategoryRepository repository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public CreateCategory(
      CategoryRepository repository, EventStore eventStore, EventPublisher publisher) {
    this.repository = repository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public CategorySummary execute(CreateCategoryCommand command) {
    if (repository
        .findByOrganizationIdAndAccountIdAndNameAndType(
            command.organizationId(), command.accountId(), command.name(), command.type())
        .isPresent()) {
      throw ApplicationException.badRequest("category already exists");
    }
    if (repository.existsByOrganizationIdAndAccountIdAndHashColor(
        command.organizationId(), command.accountId(), command.hashColor())) {
      throw ApplicationException.badRequest("color already exists");
    }
    var category =
        Category.create(
            command.organizationId(),
            command.accountId(),
            command.name(),
            command.hashColor(),
            command.type());
    var events = category.pendingEvents();

    eventStore.append(
        command.organizationId(), category.getId(), events, category.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    category.markEventsCommitted();
    return new CategorySummary(category);
  }
}
