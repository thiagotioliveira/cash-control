package dev.thiagooliveira.cashcontrol.application.user;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.outbound.UserRepository;
import dev.thiagooliveira.cashcontrol.domain.user.security.Context;
import dev.thiagooliveira.cashcontrol.domain.user.security.ContextImpl;

public class Login {

  private final AccountService accountService;
  private final UserRepository repository;

  public Login(AccountService accountService, UserRepository repository) {
    this.accountService = accountService;
    this.repository = repository;
  }

  public Context execute(String email, String password) {
    var user =
        this.repository
            .findByEmailAndPassword(email, password)
            .orElseThrow(() -> ApplicationException.badRequest("e-mail or password is incorrect"));
    var accounts = accountService.get(user.organizationId());
    if (accounts.isEmpty() || accounts.size() > 1) {
      throw ApplicationException.badRequest("invalid accounts");
    }
    return new ContextImpl(user, accounts.get(0).id());
  }
}
