package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/accounts")
public class IndexController {
  private final SecurityContext securityContext;
  private final AccountService accountService;
  private final CategoryService categoryService;
  private final TransactionService transactionService;

  public IndexController(
      SecurityContext securityContext,
      AccountService accountService,
      CategoryService categoryService,
      TransactionService transactionService) {
    this.securityContext = securityContext;
    this.accountService = accountService;
    this.categoryService = categoryService;
    this.transactionService = transactionService;
  }

  @GetMapping("/{accountId}")
  public String index(@PathVariable UUID accountId, Model model) {
    var account =
        accountService
            .get(securityContext.getUser().organizationId(), accountId)
            .orElseThrow(() -> InfrastructureException.badRequest("something wrong"));
    var categories = categoryService.get(securityContext.getUser().organizationId(), accountId);
    var today = LocalDate.now(zoneId);
    var transactions =
        transactionService.get(
            new GetTransactionsCommand(
                securityContext.getUser().organizationId(),
                accountId,
                today.with(TemporalAdjusters.firstDayOfMonth()),
                today.with(TemporalAdjusters.lastDayOfMonth())));
    var transactionsConfirmed =
        transactions.stream().filter(t -> t.status().isConfirmed()).toList();
    var transactionsScheduled =
        transactions.stream()
            .filter(t -> t.status().isScheduled())
            .sorted((t1, t2) -> t1.dueDate().compareTo(t2.dueDate()))
            .toList();
    model.addAttribute(
        "income",
        df.format(
            transactionsConfirmed.stream()
                .filter(t -> t.type().isCredit())
                .map(TransactionSummary::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute(
        "expenses",
        df.format(
            transactionsConfirmed.stream()
                .filter(t -> t.type().isDebit())
                .map(TransactionSummary::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute(
        "incomePending",
        df.format(
            transactionsScheduled.stream()
                .filter(t -> t.type().isCredit())
                .map(TransactionSummary::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute(
        "expensesPending",
        df.format(
            transactionsScheduled.stream()
                .filter(t -> t.type().isDebit())
                .map(TransactionSummary::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute("context", securityContext);
    model.addAttribute("account", new AccountModel(account));
    model.addAttribute("transactions", new TransactionListModel(accountId, transactionsConfirmed));
    model.addAttribute(
        "transactionCarouselSlide", new TransactionCarouselSlideModel(transactionsScheduled));
    model.addAttribute(
        "depositActionSheet",
        new TransactionActionSheetModel(
            accountId,
            "Deposito",
            account.bank().currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getCredit(),
            false,
            true,
            false,
            false));
    model.addAttribute(
        "withdrawActionSheet",
        new TransactionActionSheetModel(
            accountId,
            "Retirada",
            account.bank().currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getDebit(),
            false,
            true,
            false,
            false));
    model.addAttribute(
        "payableActionSheet",
        new TransactionActionSheetModel(
            accountId,
            "Pagamento",
            account.bank().currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getDebit(),
            true,
            false,
            true,
            true));
    model.addAttribute(
        "receivableActionSheet",
        new TransactionActionSheetModel(
            accountId,
            "Recebimento",
            account.bank().currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getCredit(),
            true,
            false,
            true,
            true));
    return "protected/index";
  }
}
