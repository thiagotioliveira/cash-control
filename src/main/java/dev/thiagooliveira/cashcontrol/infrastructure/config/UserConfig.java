package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.application.user.*;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.user.OrganizationEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.user.UserEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.OrganizationJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.OrganizationRepositoryAdapter;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class UserConfig {

  @Bean
  UserEventListener userEventListener(
      SecurityContext securityContext,
      UserService userService,
      UserJpaRepository userJpaRepository) {
    return new UserEventListener(securityContext, userService, userJpaRepository);
  }

  @Bean
  OrganizationEventListener organizationEventListener(
      OrganizationJpaRepository organizationJpaRepository) {
    return new OrganizationEventListener(organizationJpaRepository);
  }

  @Bean
  @Transactional
  UserService userService(
      UserJpaRepository userJpaRepository,
      OrganizationJpaRepository organizationJpaRepository,
      EventStore eventStore,
      EventPublisher publisher) {
    var userRepository = userRepository(userJpaRepository);
    return new UserServiceImpl(
        login(userRepository),
        registerUser(userRepository, eventStore, publisher),
        inviteUser(
            organizationRepository(organizationJpaRepository),
            userRepository,
            eventStore,
            publisher));
  }

  private OrganizationRepository organizationRepository(
      OrganizationJpaRepository organizationJpaRepository) {
    return new OrganizationRepositoryAdapter(organizationJpaRepository);
  }

  private UserRepository userRepository(UserJpaRepository userJpaRepository) {
    return new UserRepositoryAdapter(userJpaRepository);
  }

  private Login login(UserRepository userRepository) {
    return new Login(userRepository);
  }

  private RegisterUser registerUser(
      UserRepository userRepository, EventStore eventStore, EventPublisher publisher) {
    return new RegisterUser(userRepository, eventStore, publisher);
  }

  private InviteUser inviteUser(
      OrganizationRepository organizationRepository,
      UserRepository repository,
      EventStore eventStore,
      EventPublisher publisher) {
    return new InviteUser(organizationRepository, repository, eventStore, publisher);
  }
}
