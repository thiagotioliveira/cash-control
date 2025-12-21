package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.domain.user.Organization;
import dev.thiagooliveira.cashcontrol.domain.user.User;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;

public class RegisterUser {

  private final UserRepository userRepository;
  private final EventStore eventStore;
  private final EventPublisher publisher;

  public RegisterUser(
      UserRepository userRepository, EventStore eventStore, EventPublisher publisher) {
    this.userRepository = userRepository;
    this.eventStore = eventStore;
    this.publisher = publisher;
  }

  public UserSummary execute(RegisterUserCommand command) {

    if (userRepository.existsByEmail(command.email())) {
      throw ApplicationException.badRequest("user already registered");
    }

    var user = User.create(command.name(), command.email(), command.password());

    var organization = Organization.create(command.email());

    var events = organization.pendingEvents();
    eventStore.append(
        organization.getId(),
        user.getId(),
        organization.getId(),
        events,
        organization.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    organization.markEventsCommitted();

    user.invite(organization.getId());
    user.join();
    user.register();

    events = user.pendingEvents();
    eventStore.append(
        organization.getId(),
        user.getId(),
        user.getId(),
        events,
        user.getVersion() - events.size());
    events.forEach(publisher::publishEvent);

    user.markEventsCommitted();
    return new UserSummary(user);
  }
}
