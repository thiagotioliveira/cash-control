package dev.thiagooliveira.cashcontrol.infrastructure.persistence.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, UUID> {
  Optional<OrganizationEntity> findByEmail(String email);
}
