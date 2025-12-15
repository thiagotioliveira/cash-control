package dev.thiagooliveira.cashcontrol.domain.user;

import java.util.UUID;

public record UserSummary(UUID id, UUID organizationId, String name, String email) {

  public UserSummary(User user) {
    this(user.getId(), user.getOrganizationId(), user.getName(), user.getEmail());
  }
}
