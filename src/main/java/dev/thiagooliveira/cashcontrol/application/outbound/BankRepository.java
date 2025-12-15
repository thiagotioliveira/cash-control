package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import java.util.Optional;
import java.util.UUID;

public interface BankRepository {

  boolean existsByOrganizationIdAndName(UUID organizationId, String name);

  Optional<BankSummary> findByOrganizationIdAndId(UUID organizationId, UUID id);
}
