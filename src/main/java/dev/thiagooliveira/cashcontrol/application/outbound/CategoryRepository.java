package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import dev.thiagooliveira.cashcontrol.shared.CategoryType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  List<CategorySummary> findAllByOrganizationId(UUID organizationId);

  Optional<CategorySummary> findByOrganizationIdAndId(UUID organizationId, UUID id);

  Optional<CategorySummary> findByOrganizationIdAndNameAndType(
      UUID organizationId, String name, CategoryType type);

  boolean existsByOrganizationIdAndHashColor(UUID organizationId, String hashColor);
}
