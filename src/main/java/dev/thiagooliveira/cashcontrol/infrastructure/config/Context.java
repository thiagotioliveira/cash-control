package dev.thiagooliveira.cashcontrol.infrastructure.config;

import java.util.UUID;

public interface Context {

  UUID getOrganizationId();

  UUID getUserId();

  UUID getAccountId();
}
