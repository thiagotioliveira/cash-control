package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.account.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

  Optional<Account> findByOrganizationIdAndId(UUID organizationId, UUID id);
}
