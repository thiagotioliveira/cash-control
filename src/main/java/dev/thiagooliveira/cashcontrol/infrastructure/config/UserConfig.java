package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.outbound.EventPublisher;
import dev.thiagooliveira.cashcontrol.application.outbound.EventStore;
import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.application.user.InviteUser;
import dev.thiagooliveira.cashcontrol.application.user.RegisterUser;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.user.UserEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.UserRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

  @Bean
  UserEventListener userEventListener(UserJpaRepository userJpaRepository) {
    return new UserEventListener(userJpaRepository);
  }

  @Bean
  UserRepository userRepository(UserJpaRepository userJpaRepository) {
    return new UserRepositoryAdapter(userJpaRepository);
  }

  @Bean
  RegisterUser registerUser(
      UserRepository userRepository, EventStore eventStore, EventPublisher publisher) {
    return new RegisterUser(userRepository, eventStore, publisher);
  }

  @Bean
  InviteUser inviteUser(
      OrganizationRepository organizationRepository,
      UserRepository repository,
      EventStore eventStore,
      EventPublisher publisher) {
    return new InviteUser(organizationRepository, repository, eventStore, publisher);
  }
}
