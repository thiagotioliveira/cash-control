package dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata;

import dev.thiagooliveira.cashcontrol.infrastructure.config.Context;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("mock-data")
public class MockContext implements Context {
  private UUID organizationId;
  private UUID userId;
  private UUID accountId;

  public MockContext() {}

  public MockContext(UUID organizationId, UUID userId, UUID accountId) {
    this.organizationId = organizationId;
    this.userId = userId;
    this.accountId = accountId;
  }

  public UUID getOrganizationId() {
    return organizationId;
  }

  public UUID getUserId() {
    return userId;
  }

  public UUID getAccountId() {
    return accountId;
  }

  public void setOrganizationId(UUID organizationId) {
    this.organizationId = organizationId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public void setAccountId(UUID accountId) {
    this.accountId = accountId;
  }
}
