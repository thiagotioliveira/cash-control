package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.domain.user.UserSummary;

public class Login {

  private final UserRepository repository;

  public Login(UserRepository repository) {
    this.repository = repository;
  }

  public UserSummary execute(String email, String password) {
    return this.repository
        .findByEmailAndPassword(email, password)
        .orElseThrow(() -> ApplicationException.badRequest("e-mail or password is incorrect"));
  }
}
