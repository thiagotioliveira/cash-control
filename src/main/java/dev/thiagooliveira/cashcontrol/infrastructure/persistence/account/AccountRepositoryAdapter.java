package dev.thiagooliveira.cashcontrol.infrastructure.persistence.account;

import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountRepositoryAdapter implements AccountRepository {

  private final AccountJpaRepository repository;

  public AccountRepositoryAdapter(AccountJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<AccountSummary> findByOrganizationIdAndId(UUID organizationId, UUID id) {
    return this.repository
        .findByOrganizationIdAndId(organizationId, id)
        .map(AccountEntity::toDomain);
  }

  @Override
  public List<AccountSummary> findAllByOrganizationId(UUID organizationId) {
    return this.repository.findAllByOrganizationId(organizationId).stream()
        .map(AccountEntity::toDomain)
        .toList();
  }
}
