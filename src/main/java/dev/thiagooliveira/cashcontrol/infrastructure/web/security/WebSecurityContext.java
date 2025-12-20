package dev.thiagooliveira.cashcontrol.infrastructure.web.security;

import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import org.springframework.stereotype.Component;

@Component
public class WebSecurityContext implements SecurityContext {

  private UserSummary user;

  public WebSecurityContext() {}

  @Override
  public void setUser(UserSummary user) {
    if (this.user == null) {
      this.user = user;
    } else {
      throw InfrastructureException.conflict("SecurityContext already set");
    }
  }

  @Override
  public void invalidate() {
    this.user = null;
  }

  @Override
  public UserSummary getUser() {
    return this.user;
  }
}
