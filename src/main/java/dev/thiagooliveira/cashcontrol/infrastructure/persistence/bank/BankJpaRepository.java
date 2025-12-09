package dev.thiagooliveira.cashcontrol.infrastructure.persistence.bank;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankJpaRepository extends JpaRepository<BankEntity, UUID> {
  boolean existsByOrganizationIdAndName(UUID organizationId, String name);
}
