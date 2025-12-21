package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.application.user.dto.InviteUserCommand;
import dev.thiagooliveira.cashcontrol.domain.user.User;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;

public class InviteUser {

  private final OrganizationRepository organizationRepository;
  private final UserRepository repository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public InviteUser(
      OrganizationRepository organizationRepository,
      UserRepository repository,
      EventStore eventStore,
      EventPublisher publisher) {
    this.organizationRepository = organizationRepository;
    this.repository = repository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public UserSummary execute(InviteUserCommand command) {
    if (this.repository.existsByEmail(command.email())) {
      throw ApplicationException.badRequest("user already registered");
    }
    organizationRepository
        .findById(command.organizationId())
        .orElseThrow(() -> ApplicationException.notFound("organization not found"));

    var user = User.create(command.name(), command.email());
    user.invite(command.organizationId());

    var events = user.pendingEvents();
    eventStore.append(
        command.organizationId(),
        command.userId(),
        user.getId(),
        events,
        user.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    user.markEventsCommitted();
    return new UserSummary(user);
  }
}
