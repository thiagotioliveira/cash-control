package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockDataProperties;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AccountModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.ListCategoryModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransactionListModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransactionsModel;
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
  private final MockDataProperties properties;
  private final AccountRepository accountRepository;
  private final BankRepository bankRepository;
  private final CategoryRepository categoryRepository;
  private final GetTransactions getTransactions;

  public IndexController(
      MockDataProperties properties,
      AccountRepository accountRepository,
      BankRepository bankRepository,
      CategoryRepository categoryRepository,
      GetTransactions getTransactions) {
    this.properties = properties;
    this.accountRepository = accountRepository;
    this.bankRepository = bankRepository;
    this.categoryRepository = categoryRepository;
    this.getTransactions = getTransactions;
  }

  @GetMapping({"", "/"})
  public String index(Model model) {
    var account =
        accountRepository
            .findByOrganizationIdAndId(properties.getOrganizationId(), properties.getAccountId())
            .orElseThrow(() -> InfrastructureException.badRequest("something wrong"));
    var bank =
        bankRepository
            .findByOrganizationIdAndId(properties.getOrganizationId(), account.getBankId())
            .orElseThrow(() -> InfrastructureException.badRequest("something wrong"));
    var today = LocalDate.now();
    var categories = categoryRepository.findAllByOrganizationId(properties.getOrganizationId());
    var transactions =
        getTransactions.execute(
            new GetTransactionsCommand(
                properties.getOrganizationId(),
                properties.getAccountId(),
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
                .map(GetTransactionItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute(
        "expenses",
        df.format(
            transactionsConfirmed.stream()
                .filter(t -> t.type().isDebit())
                .map(GetTransactionItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute(
        "incomePending",
        df.format(
            transactionsScheduled.stream()
                .filter(t -> t.type().isCredit())
                .map(GetTransactionItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute(
        "expensesPending",
        df.format(
            transactionsScheduled.stream()
                .filter(t -> t.type().isDebit())
                .map(GetTransactionItem::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)));
    model.addAttribute("account", new AccountModel(df, account, bank));
    model.addAttribute("categories", new ListCategoryModel(categories));
    model.addAttribute("transactions", new TransactionListModel(transactionsConfirmed));
    model.addAttribute("bills", new TransactionsModel(transactionsScheduled));
    return "protected/index";
  }
}
