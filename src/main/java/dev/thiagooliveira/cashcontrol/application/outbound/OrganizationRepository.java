package dev.thiagooliveira.cashcontrol.application.outbound;

import dev.thiagooliveira.cashcontrol.application.user.dto.GetOrganizationItem;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository {

  Optional<GetOrganizationItem> findById(UUID id);

  Optional<GetOrganizationItem> findByEmail(String email);
}
