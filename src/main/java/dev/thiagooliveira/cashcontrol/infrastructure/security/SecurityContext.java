package dev.thiagooliveira.cashcontrol.infrastructure.security;

import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import dev.thiagooliveira.cashcontrol.domain.user.security.Context;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SecurityContext implements Context {

  private Context context;

  public SecurityContext() {}

  public void setContext(Context context) {
    if (this.context == null) {
      this.context = context;
    } else throw InfrastructureException.conflict("inconsistent context");
  }

  @Override
  public UserSummary getUser() {
    return context.getUser();
  }

  @Override
  public UUID getAccountId() {
    return context.getAccountId();
  }
}
