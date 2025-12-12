package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.application.account.dto.GetAccountItem;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

  Optional<GetAccountItem> findByOrganizationIdAndId(UUID organizationId, UUID id);
}
