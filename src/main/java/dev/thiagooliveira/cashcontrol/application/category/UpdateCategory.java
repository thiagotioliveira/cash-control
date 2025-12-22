package dev.thiagooliveira.cashcontrol.application.category;

import dev.thiagooliveira.cashcontrol.application.category.dto.UpdateCategoryCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;

public class UpdateCategory {

  private final CategoryRepository repository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public UpdateCategory(
      CategoryRepository repository, EventStore eventStore, EventPublisher publisher) {
    this.repository = repository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public CategorySummary execute(UpdateCategoryCommand command) {
    this.repository
        .findAllByOrganizationIdAndHashColor(command.organizationId(), command.hashColor())
        .stream()
        .filter(c -> !c.id().equals(command.categoryId()))
        .findFirst()
        .ifPresent(
            c -> {
              throw ApplicationException.badRequest("color already exists");
            });

    var categorySummary =
        this.repository
            .findByOrganizationIdAndId(command.organizationId(), command.categoryId())
            .orElseThrow(() -> ApplicationException.badRequest("category does not exist"));

    if (this.repository
            .findByOrganizationIdAndNameAndType(
                command.organizationId(), command.name(), categorySummary.type())
            .stream()
            .filter(c -> !c.id().equals(command.categoryId()))
            .count()
        > 0) {
      throw ApplicationException.badRequest("category already exists");
    }

    var pastEvents = eventStore.load(command.organizationId(), command.categoryId());

    if (pastEvents.isEmpty()) {
      throw ApplicationException.notFound("category not found");
    }

    var category = Category.rehydrate(pastEvents);
    category.update(command.name(), command.hashColor(), command.userId());

    var newEvents = category.pendingEvents();
    eventStore.append(
        command.organizationId(),
        command.userId(),
        category.getId(),
        newEvents,
        category.getVersion() - newEvents.size());
    newEvents.forEach(publisher::publishEvent);

    category.markEventsCommitted();

    return new CategorySummary(category);
  }
}
