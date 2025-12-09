package dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank;

import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;

public class BankRepositoryAdapter implements BankRepository {

  private final BankJpaRepository repository;

  public BankRepositoryAdapter(BankJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean existsByName(String name) {
    return this.repository.existsByName(name);
  }
}
