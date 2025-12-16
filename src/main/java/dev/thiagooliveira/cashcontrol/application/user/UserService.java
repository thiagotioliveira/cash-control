package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.user.dto.InviteUserCommand;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;
import dev.thiagooliveira.cashcontrol.domain.user.security.Context;

public interface UserService {

  UserSummary register(RegisterUserCommand command);

  UserSummary invite(InviteUserCommand command);

  Context login(String email, String password);
}
