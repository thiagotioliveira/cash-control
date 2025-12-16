package dev.thiagooliveira.cashcontrol.domain.user.security;

import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import java.util.UUID;

public interface Context {

  UserSummary getUser();

  UUID getAccountId();
}
