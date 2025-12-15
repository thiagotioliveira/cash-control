package dev.thiagooliveira.cashcontrol.application.category;

import dev.thiagooliveira.cashcontrol.application.category.dto.CreateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.category.Category;

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

  public Category execute(CreateCategoryCommand command) {
    if (repository
        .findByOrganizationIdAndNameAndType(
            command.organizationId(), command.name(), command.type())
        .isPresent()) {
      throw ApplicationException.badRequest("category already exists");
    }
    var category =
        Category.create(
            command.organizationId(), command.name(), command.hashColor(), command.type());
    var events = category.pendingEvents();

    eventStore.append(
        command.organizationId(), category.getId(), events, category.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    category.markEventsCommitted();
    return category;
  }
}
