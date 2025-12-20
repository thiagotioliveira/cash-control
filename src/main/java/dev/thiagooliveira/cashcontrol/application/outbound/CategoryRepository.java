package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.category.CategorySummary;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

  List<CategorySummary> findAllByOrganizationIdAndAccountId(UUID organizationId, UUID accountId);

  Optional<CategorySummary> findByOrganizationIdAndAccountIdAndId(
      UUID organizationId, UUID accountId, UUID id);

  Optional<CategorySummary> findByOrganizationIdAndAccountIdAndNameAndType(
      UUID organizationId, UUID accountId, String name, TransactionType type);

  boolean existsByOrganizationIdAndAccountIdAndHashColor(
      UUID organizationId, UUID accountId, String hashColor);
}
