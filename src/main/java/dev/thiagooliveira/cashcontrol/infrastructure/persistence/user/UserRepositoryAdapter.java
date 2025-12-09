package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;

public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository repository;

  public UserRepositoryAdapter(UserJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }
}
