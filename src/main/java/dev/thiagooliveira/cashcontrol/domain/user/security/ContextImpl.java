package dev.thiagooliveira.cashcontrol.domain.user.security;

import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import java.util.UUID;

public class ContextImpl implements Context {

  private final UserSummary user;
  private final UUID accountId;

  public ContextImpl(UserSummary user, UUID accountId) {
    this.user = user;
    this.accountId = accountId;
  }

  @Override
  public UserSummary getUser() {
    return this.user;
  }

  @Override
  public UUID getAccountId() {
    return this.accountId;
  }
}
