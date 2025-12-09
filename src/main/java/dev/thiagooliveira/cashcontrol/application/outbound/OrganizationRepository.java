package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.domain.user.Organization;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {

  Optional<Organization> findById(UUID id);

  Optional<Organization> findByEmail(String email);
}
