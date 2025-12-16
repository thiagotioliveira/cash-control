package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmailAndPassword(String email, String password);
}
