package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.zoneId;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.transfer.TransferService;
import dev.thiagooliveira.cashcontrol.application.transfer.dto.CreateTransferCommand;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AlertModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransferActionSheetModel;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/accounts")
public class TransferController {

  private final SecurityContext securityContext;
  private final AccountService accountService;
  private final CategoryService categoryService;
  private final TransferService transferService;

  public TransferController(
      SecurityContext securityContext,
      AccountService accountService,
      CategoryService categoryService,
      TransferService transferService) {
    this.securityContext = securityContext;
    this.accountService = accountService;
    this.categoryService = categoryService;
    this.transferService = transferService;
  }

  @PostMapping("{accountId}/transfers/review")
  public String postReview(
      @PathVariable UUID accountId,
      @ModelAttribute TransferActionSheetModel.TransferForm form,
      Model model) {
    var categoryFrom =
        categoryService
            .get(securityContext.getUser().organizationId(), accountId, form.getCategoryIdFrom())
            .orElseThrow(() -> InfrastructureException.notFound("Category from not found"));
    form.setCategoryNameFrom(categoryFrom.name());
    var categoryTo =
        categoryService
            .get(
                securityContext.getUser().organizationId(),
                form.getAccountIdTo(),
                form.getCategoryIdTo())
            .orElseThrow(() -> InfrastructureException.notFound("Category to not found"));
    form.setCategoryNameTo(categoryTo.name());

    var accountFrom =
        accountService
            .get(securityContext.getUser().organizationId(), accountId)
            .orElseThrow(() -> InfrastructureException.notFound("Account from not found"));
    form.setAccountIdFrom(accountId);
    form.setAccountNameFrom(accountFrom.name());
    var accountTo =
        accountService
            .get(securityContext.getUser().organizationId(), form.getAccountIdTo())
            .orElseThrow(() -> InfrastructureException.notFound("Account to not found"));
    form.setAccountNameTo(accountTo.name());
    model.addAttribute("transfer", form);
    return "protected/transfers/transfer-review";
  }

  @PostMapping("{accountId}/transfers")
  public String postTransfer(
      @PathVariable UUID accountId,
      @ModelAttribute TransferActionSheetModel.TransferForm form,
      Model model) {
    try {
      this.transferService.create(
          new CreateTransferCommand(
              this.securityContext.getUser().organizationId(),
              this.securityContext.getUser().id(),
              form.getAccountIdFrom(),
              form.getAccountIdTo(),
              form.getCategoryIdFrom(),
              form.getCategoryIdTo(),
              form.getDescription(),
              form.getOccurredAt().atZone(zoneId).toInstant(),
              form.getAmountFrom(),
              form.getAmountTo()));
      return String.format("redirect:/protected/accounts/%s/transactions", accountId);
    } catch (ApplicationException | DomainException e) {
      model.addAttribute("alert", AlertModel.error(e.getMessage()));
      model.addAttribute("transfer", form);
      model.addAttribute("alert", AlertModel.error(e.getMessage()));
      return String.format("/protected/accounts/%s/transfers/review", accountId);
    }
  }
}
