package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.application.category.dto.GetCategoryItem;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  List<GetCategoryItem> findAllByOrganizationId(UUID organizationId);

  Optional<GetCategoryItem> findByOrganizationIdAndId(UUID organizationId, UUID id);

  Optional<GetCategoryItem> findByOrganizationIdAndNameAndType(
      UUID organizationId, String name, TransactionType type);
}
