package dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank;

import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import java.util.Optional;
import java.util.UUID;

public class BankRepositoryAdapter implements BankRepository {

  private final BankJpaRepository repository;

  public BankRepositoryAdapter(BankJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean existsByOrganizationIdAndName(UUID organizationId, String name) {
    return this.repository.existsByOrganizationIdAndName(organizationId, name);
  }

  @Override
  public Optional<BankSummary> findByOrganizationIdAndId(UUID organizationId, UUID id) {
    return this.repository.findByOrganizationIdAndId(organizationId, id).map(BankEntity::toDomain);
  }
}
