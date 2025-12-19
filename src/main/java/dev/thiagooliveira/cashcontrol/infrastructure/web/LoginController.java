package dev.thiagooliveira.cashcontrol.infrastructure.web;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.user.UserService;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AlertModel;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {

  private final SecurityContext securityContext;
  private final UserService userService;
  private final AccountService accountService;

  public LoginController(
      SecurityContext securityContext, UserService userService, AccountService accountService) {
    this.securityContext = securityContext;
    this.userService = userService;
    this.accountService = accountService;
  }

  @GetMapping
  public String login(Model model) {
    model.addAttribute("form", new LoginForm());
    return "login";
  }

  @PostMapping
  @Transactional
  public String login(@ModelAttribute LoginForm form, Model model) {
    try {
      var user = this.userService.login(form.getEmail(), form.getPassword());
      var account =
          this.accountService.get(user.organizationId()).stream()
              .findFirst()
              .orElseThrow(() -> InfrastructureException.notFound("account not found"));
      this.securityContext.setUser(user);
      return "redirect:/protected/accounts/" + account.id();
    } catch (ApplicationException | DomainException e) {
      model.addAttribute("form", form);
      model.addAttribute("alert", AlertModel.error(e.getMessage()));
      return "login";
    }
  }

  public static class LoginForm {
    private String email;
    private String password;

    public LoginForm() {}

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
  }
}
