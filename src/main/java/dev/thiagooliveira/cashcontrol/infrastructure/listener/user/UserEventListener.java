package dev.thiagooliveira.cashcontrol.infrastructure.listener.user;

import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserCreated;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserInvited;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserJoined;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserJpaRepository;
import org.springframework.context.event.EventListener;

public class UserEventListener {

  private final UserJpaRepository repository;

  public UserEventListener(UserJpaRepository repository) {
    this.repository = repository;
  }

  @EventListener
  public void on(UserCreated event) {
    var user = new UserEntity(event);
    repository.save(user);
  }

  @EventListener
  public void on(UserInvited event) {
    var user =
        repository
            .findById(event.userId())
            .orElseThrow(() -> InfrastructureException.notFound("User not found"));
    user.invite(event);
    repository.save(user);
  }

  @EventListener
  public void on(UserJoined event) {
    var user =
        repository
            .findById(event.userId())
            .orElseThrow(() -> InfrastructureException.notFound("User not found"));
    user.join(event);
    repository.save(user);
  }
}
