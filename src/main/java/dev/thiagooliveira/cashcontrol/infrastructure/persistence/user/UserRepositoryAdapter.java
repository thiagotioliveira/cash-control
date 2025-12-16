package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import java.util.Optional;

public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository repository;

  public UserRepositoryAdapter(UserJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public Optional<UserSummary> findByEmailAndPassword(String email, String password) {
    return this.repository.findByEmailAndPassword(email, password).map(UserEntity::toDomain);
  }
}
