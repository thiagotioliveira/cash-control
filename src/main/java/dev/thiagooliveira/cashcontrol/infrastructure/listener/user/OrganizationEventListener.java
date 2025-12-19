package dev.thiagooliveira.cashcontrol.infrastructure.listener.user;

import dev.thiagooliveira.cashcontrol.domain.event.user.v1.OrganizationCreated;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.OrganizationEntity;
import dev.thiagooliveira.cashcontrol.infrastructure.persistence.user.OrganizationJpaRepository;
import org.springframework.context.event.EventListener;

public class OrganizationEventListener {

  private final OrganizationJpaRepository repository;

  public OrganizationEventListener(OrganizationJpaRepository repository) {
    this.repository = repository;
  }

  @EventListener
  public void on(OrganizationCreated event) {
    var organization = new OrganizationEntity(event);
    repository.save(organization);
  }
}
