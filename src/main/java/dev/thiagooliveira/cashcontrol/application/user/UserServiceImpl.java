package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.user.dto.InviteUserCommand;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;

public class UserServiceImpl implements UserService {

  private final Login login;
  private final RegisterUser registerUser;
  private final InviteUser inviteUser;

  public UserServiceImpl(Login login, RegisterUser registerUser, InviteUser inviteUser) {
    this.login = login;
    this.registerUser = registerUser;
    this.inviteUser = inviteUser;
  }

  @Override
  public UserSummary register(RegisterUserCommand command) {
    return this.registerUser.execute(command);
  }

  @Override
  public UserSummary invite(InviteUserCommand command) {
    return this.inviteUser.execute(command);
  }

  @Override
  public UserSummary login(String email, String password) {
    return this.login.execute(email, password);
  }
}
