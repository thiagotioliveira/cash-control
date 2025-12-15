package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

  Optional<AccountSummary> findByOrganizationIdAndId(UUID organizationId, UUID id);
}
