package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected")
public class IndexController {
  private final MockContext context;
  private final AccountRepository accountRepository;
  private final CategoryRepository categoryRepository;
  private final GetTransactions getTransactions;

  public IndexController(
      MockContext context,
      AccountRepository accountRepository,
      CategoryRepository categoryRepository,
      GetTransactions getTransactions) {
    this.context = context;
    this.accountRepository = accountRepository;
    this.categoryRepository = categoryRepository;
    this.getTransactions = getTransactions;
  }

  @GetMapping
  public String index(Model model) {
    var account =
        accountRepository
            .findByOrganizationIdAndId(context.getOrganizationId(), context.getAccountId())
            .orElseThrow(() -> InfrastructureException.badRequest("something wrong"));
    var categories = categoryRepository.findAllByOrganizationId(context.getOrganizationId());
    var today = LocalDate.now(zoneId);
    var transactions =
        getTransactions.execute(
            new GetTransactionsCommand(
                context.getOrganizationId(),
                context.getAccountId(),
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
    model.addAttribute("account", new AccountModel(account));
    model.addAttribute("transactions", new TransactionListModel(transactionsConfirmed));
    model.addAttribute(
        "transactionCarouselSlide", new TransactionCarouselSlideModel(transactionsScheduled));
    model.addAttribute(
        "depositActionSheet",
        new TransactionActionSheetModel(
            "Deposito",
            account.currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getCredit(),
            false,
            true,
            false,
            false));
    model.addAttribute(
        "withdrawActionSheet",
        new TransactionActionSheetModel(
            "Retirada",
            account.currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getDebit(),
            false,
            true,
            false,
            false));
    model.addAttribute(
        "payableActionSheet",
        new TransactionActionSheetModel(
            "Pagamento",
            account.currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getDebit(),
            true,
            false,
            true,
            true));
    model.addAttribute(
        "receivableActionSheet",
        new TransactionActionSheetModel(
            "Recebimento",
            account.currency(),
            new TransactionActionSheetModel.ListCategoryModel(categories).getCredit(),
            true,
            false,
            true,
            true));
    return "protected/index";
  }
}
