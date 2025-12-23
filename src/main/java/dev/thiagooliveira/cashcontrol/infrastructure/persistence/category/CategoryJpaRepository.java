package dev.thiagooliveira.cashcontrol.infrastructure.persistence.category;

import dev.thiagooliveira.cashcontrol.shared.CategoryType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

  Optional<CategoryEntity> findByOrganizationIdAndId(UUID organizationId, UUID id);

  Optional<CategoryEntity> findByOrganizationIdAndNameAndType(
      UUID organizationId, String name, CategoryType type);

  List<CategoryEntity> findAllByOrganizationIdAndHashColor(UUID organizationId, String hashColor);

  List<CategoryEntity> findAllByOrganizationIdOrderByNameAscTypeAsc(UUID organizationId);
}
