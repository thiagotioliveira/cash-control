package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.account.dto.CreateAccountCommand;
import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AccountActionSheetModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AccountModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AlertModel;
import dev.thiagooliveira.cashcontrol.shared.Currency;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/accounts")
public class AccountController {

  private final SecurityContext securityContext;
  private final AccountService accountService;
  private final BankService bankService;

  public AccountController(
      SecurityContext securityContext, AccountService accountService, BankService bankService) {
    this.securityContext = securityContext;
    this.accountService = accountService;
    this.bankService = bankService;
  }

  @GetMapping
  public String index(Model model) {
    var accounts = this.accountService.get(securityContext.getUser().organizationId());
    model.addAttribute("accounts", accounts.stream().map(AccountModel::new).toList());
    model.addAttribute("account", new AccountActionSheetModel());
    return "protected/accounts/account-list";
  }

  @PostMapping
  public String postAccount(@ModelAttribute AccountActionSheetModel.AccountForm form, Model model) {
    var organizationId = securityContext.getUser().organizationId();
    var userId = securityContext.getUser().id();
    try {
      var bankId =
          bankService
              .get(organizationId, form.getBankName())
              .map(BankSummary::id)
              .orElseGet(
                  () ->
                      bankService
                          .createBank(
                              new CreateBankCommand(
                                  organizationId, userId, form.getBankName(), Currency.EUR))
                          .id());

      accountService.createAccount(
          new CreateAccountCommand(organizationId, userId, bankId, form.getName()));
      model.addAttribute("alert", AlertModel.success("Conta criada com sucesso!"));
    } catch (ApplicationException | DomainException e) {
      model.addAttribute("alert", AlertModel.error(e.getMessage()));
    }

    return index(model);
  }
}
