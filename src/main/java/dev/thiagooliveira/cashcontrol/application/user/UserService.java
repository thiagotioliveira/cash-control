package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.user.dto.InviteUserCommand;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;

public interface UserService {

  UserSummary register(RegisterUserCommand command);

  UserSummary invite(InviteUserCommand command);

  UserSummary login(String email, String password);
}
