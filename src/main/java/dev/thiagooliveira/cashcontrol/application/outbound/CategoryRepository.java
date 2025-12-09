package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.category.Category;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  Optional<Category> findByOrganizationIdAndId(UUID organizationId, UUID id);

  Optional<Category> findByOrganizationIdAndNameAndType(
      UUID organizationId, String name, TransactionType type);
}
