package dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank;

import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
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
}
