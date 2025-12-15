package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.user.OrganizationSummary;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {

  Optional<OrganizationSummary> findById(UUID id);

  Optional<OrganizationSummary> findByEmail(String email);
}
