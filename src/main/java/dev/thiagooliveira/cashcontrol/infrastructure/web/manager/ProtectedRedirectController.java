package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected")
public class ProtectedRedirectController {

  private final SecurityContext securityContext;
  private final AccountService accountService;

  public ProtectedRedirectController(
      SecurityContext securityContext, AccountService accountService) {
    this.securityContext = securityContext;
    this.accountService = accountService;
  }

  @GetMapping
  public String redirect() {
    if (securityContext.getUser() != null) {
      var account = getAccountSummary(securityContext.getUser().organizationId());
      return String.format("redirect:/protected/accounts/%s", account.id());
    } else {
      return "redirect:/register";
    }
  }

  @GetMapping("/reports")
  public String redirectReport() {
    if (securityContext.getUser() != null) {
      var account = getAccountSummary(securityContext.getUser().organizationId());
      return String.format("redirect:/protected/accounts/%s/reports", account.id());
    } else {
      return "redirect:/register";
    }
  }

  private AccountSummary getAccountSummary(UUID organizationId) {
    return this.accountService.get(securityContext.getUser().organizationId()).stream()
        .findFirst()
        .orElseThrow(() -> InfrastructureException.conflict("Account not found"));
  }
}
