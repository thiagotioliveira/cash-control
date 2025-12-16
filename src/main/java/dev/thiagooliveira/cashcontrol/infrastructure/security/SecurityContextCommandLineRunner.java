package dev.thiagooliveira.cashcontrol.infrastructure.security;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.account.dto.CreateAccountCommand;
import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.user.PasswordUtils;
import dev.thiagooliveira.cashcontrol.application.user.UserService;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.domain.user.security.ContextImpl;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextCommandLineRunner implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(SecurityContextCommandLineRunner.class);

  @Autowired private SecurityContext securityContext;
  @Autowired private UserService userService;
  @Autowired private BankService bankService;
  @Autowired private AccountService accountService;

  @Value("${app.user.name}")
  private String user;

  @Value("${app.user.email}")
  private String email;

  @Value("${app.user.password}")
  private String password;

  @Value("${app.bank.name")
  private String bankName;

  @Value("${app.bank.currency}")
  private String currency;

  @Value("${app.account.name}")
  private String accountName;

  @Override
  public void run(String... args) throws Exception {
    if (StringUtils.isBlank(user) || StringUtils.isBlank(email))
      throw InfrastructureException.conflict("inconsistent user");
    if (StringUtils.isBlank(password)) {
      var password = PasswordUtils.generatePassword();
      log.info("Registering user '{}' with password '{}'", user, password);
      var userRegistered =
          userService.register(new RegisterUserCommand(user, email, password, password));
      var bank =
          bankService.createBank(
              new CreateBankCommand(
                  userRegistered.organizationId(), bankName, Currency.valueOf(currency)));
      var account =
          accountService.createAccount(
              new CreateAccountCommand(userRegistered.organizationId(), bank.id(), accountName));
      securityContext.setContext(new ContextImpl(userRegistered, account.id()));
    } else {
      log.info("Loging with user '{}' with password '{}'", user, password);
      securityContext.setContext(userService.login(email, password));
    }
  }
}
