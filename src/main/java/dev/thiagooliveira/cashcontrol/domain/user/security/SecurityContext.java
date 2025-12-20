package dev.thiagooliveira.cashcontrol.domain.user.security;

import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;

public interface SecurityContext {

  UserSummary getUser();

  void setUser(UserSummary user);

  void invalidate();
}
