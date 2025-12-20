package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

  Optional<CategoryEntity> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  Optional<CategoryEntity> findByOrganizationIdAndAccountIdAndNameAndType(
      UUID organizationId, UUID accountId, String name, TransactionType type);

  boolean existsByOrganizationIdAndAccountIdAndHashColor(
      UUID organizationId, UUID accountId, String hashColor);

  List<CategoryEntity> findAllByOrganizationIdAndAccountId(UUID organizationId, UUID accountId);
}
