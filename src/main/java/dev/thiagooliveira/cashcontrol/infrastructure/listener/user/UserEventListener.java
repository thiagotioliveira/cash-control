package dev.thiagooliveira.cashcontrol.infrastructure.listener.user;

import dev.thiagooliveira.cashcontrol.application.user.UserService;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserCreated;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserInvited;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserJoined;
import dev.thiagooliveira.cashcontrol.domain.event.user.v1.UserRegistered;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserJpaRepository;
import java.util.UUID;
import org.springframework.context.event.EventListener;

public class UserEventListener {

  private final SecurityContext securityContext;
  private final UserService userService;
  private final UserJpaRepository repository;

  public UserEventListener(
      SecurityContext securityContext, UserService userService, UserJpaRepository repository) {
    this.securityContext = securityContext;
    this.userService = userService;
    this.repository = repository;
  }

  @EventListener
  public void on(UserCreated event) {
    var user = new UserEntity(event);
    repository.save(user);
  }

  @EventListener
  public void on(UserInvited event) {
    var user = findById(event.userId());
    user.invite(event);
    repository.save(user);
  }

  @EventListener
  public void on(UserJoined event) {
    var user = findById(event.userId());
    user.join(event);
    repository.save(user);
  }

  @EventListener
  public void on(UserRegistered event) {
    var user = findById(event.userId());
    this.securityContext.setUser(this.userService.login(user.getEmail(), user.getPassword()));
  }

  private UserEntity findById(UUID id) {
    return repository
        .findById(id)
        .orElseThrow(() -> InfrastructureException.notFound("User not found"));
  }
}
