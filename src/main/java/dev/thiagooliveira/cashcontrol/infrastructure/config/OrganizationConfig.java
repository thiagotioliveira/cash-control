package dev.thiagooliveira.cashcontrol.infrastructure.config;

import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.listener.user.OrganizationEventListener;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.OrganizationJpaRepository;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.OrganizationRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrganizationConfig {

  @Bean
  OrganizationEventListener organizationEventListener(
      OrganizationJpaRepository organizationJpaRepository) {
    return new OrganizationEventListener(organizationJpaRepository);
  }

  @Bean
  OrganizationRepository organizationRepository(
      OrganizationJpaRepository organizationJpaRepository) {
    return new OrganizationRepositoryAdapter(organizationJpaRepository);
  }
}
