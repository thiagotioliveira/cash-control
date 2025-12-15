package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.domain.user.OrganizationSummary;
import java.util.Optional;
import java.util.UUID;

public class OrganizationRepositoryAdapter implements OrganizationRepository {

  private final OrganizationJpaRepository repository;

  public OrganizationRepositoryAdapter(OrganizationJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<OrganizationSummary> findById(UUID id) {
    return this.repository.findById(id).map(OrganizationEntity::toDomain);
  }

  @Override
  public Optional<OrganizationSummary> findByEmail(String email) {
    return this.repository.findByEmail(email).map(OrganizationEntity::toDomain);
  }
}
