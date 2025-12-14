package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import dev.thiagooliveira.cashcontrol.application.outbound.OrganizationRepository;
import dev.thiagooliveira.cashcontrol.application.user.dto.GetOrganizationItem;
import java.util.Optional;
import java.util.UUID;

public class OrganizationRepositoryAdapter implements OrganizationRepository {

  private final OrganizationJpaRepository repository;

  public OrganizationRepositoryAdapter(OrganizationJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<GetOrganizationItem> findById(UUID id) {
    return this.repository.findById(id).map(OrganizationEntity::toDomain);
  }

  @Override
  public Optional<GetOrganizationItem> findByEmail(String email) {
    return this.repository.findByEmail(email).map(OrganizationEntity::toDomain);
  }
}
