package dev.thiagooliveira.cashcontrol.infrastructure.web;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.account.dto.CreateAccountCommand;
import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.user.UserService;
import dev.thiagooliveira.cashcontrol.application.user.dto.RegisterUserCommand;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AlertModel;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/register")
public class RegisterController {
  private final SecurityContext securityContext;
  private final PlatformTransactionManager txManager;
  private final UserService userService;
  private final BankService bankService;
  private final AccountService accountService;

  public RegisterController(
      SecurityContext securityContext,
      PlatformTransactionManager txManager,
      UserService userService,
      BankService bankService,
      AccountService accountService) {
    this.securityContext = securityContext;
    this.txManager = txManager;
    this.userService = userService;
    this.bankService = bankService;
    this.accountService = accountService;
  }

  @GetMapping
  public String register(Model model) {
    model.addAttribute("form", new RegisterForm());
    return "register";
  }

  @PostMapping
  public String register(@ModelAttribute RegisterForm form, Model model) {
    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
    try {
      var user =
          this.userService.register(
              new RegisterUserCommand(
                  form.username, form.email, form.password, form.confirmPassword));
      var bank =
          this.bankService.createBank(
              new CreateBankCommand(user.organizationId(), user.id(), form.bankName, Currency.EUR));
      var account =
          this.accountService.createAccount(
              new CreateAccountCommand(
                  user.organizationId(), user.id(), bank.id(), form.accountName));
      txManager.commit(status);
      return "redirect:/protected/accounts/" + account.id();
    } catch (ApplicationException | DomainException e) {
      txManager.rollback(status);
      securityContext.invalidate();
      model.addAttribute("form", form);
      model.addAttribute("alert", AlertModel.error(e.getMessage()));
      return "register";
    }
  }

  public static class RegisterForm {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private String bankName;
    private String accountName;

    public RegisterForm() {}

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }

    public String getConfirmPassword() {
      return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
      this.confirmPassword = confirmPassword;
    }

    public String getBankName() {
      return bankName;
    }

    public void setBankName(String bankName) {
      this.bankName = bankName;
    }

    public String getAccountName() {
      return accountName;
    }

    public void setAccountName(String accountName) {
      this.accountName = accountName;
    }
  }
}
