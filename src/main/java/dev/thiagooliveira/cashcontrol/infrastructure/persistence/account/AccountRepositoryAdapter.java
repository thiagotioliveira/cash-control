package dev.thiagooliveira.cashcontrol.infrastructure.persistence.account;

import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.domain.account.Account;
import java.util.Optional;
import java.util.UUID;

public class AccountRepositoryAdapter implements AccountRepository {

  private final AccountJpaRepository repository;

  public AccountRepositoryAdapter(AccountJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Optional<Account> findByOrganizationIdAndId(UUID organizationId, UUID id) {
    return this.repository
        .findByOrganizationIdAndId(organizationId, id)
        .map(AccountEntity::toDomain);
  }
}
