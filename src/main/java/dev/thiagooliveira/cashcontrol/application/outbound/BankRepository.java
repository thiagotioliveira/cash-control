package dev.thiagooliveira.cashcontrol.application.outbound;

import java.util.UUID;

public interface BankRepository {

  boolean existsByOrganizationIdAndName(UUID organizationId, String name);
}
