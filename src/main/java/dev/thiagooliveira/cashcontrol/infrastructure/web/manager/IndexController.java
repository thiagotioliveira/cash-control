package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import dev.thiagooliveira.cashcontrol.application.outbound.AccountRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
import dev.thiagooliveira.cashcontrol.application.outbound.TransactionRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockDataProperties;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AccountModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransactionsModel;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected")
public class IndexController {

  private static final DecimalFormat df;
  private static final DateTimeFormatter dtf;
  private static final DateTimeFormatter dtfHourOfDay;

  private final MockDataProperties properties;
  private final AccountRepository accountRepository;
  private final BankRepository bankRepository;
  private final TransactionRepository transactionRepository;

  static {
    df = (DecimalFormat) DecimalFormat.getInstance(Locale.of("pt", "PT"));
    df.applyPattern("#,##0.00");

    dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    dtfHourOfDay = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
  }

  private final GetTransactions getTransactions;

  public IndexController(
      MockDataProperties properties,
      AccountRepository accountRepository,
      BankRepository bankRepository,
      TransactionRepository transactionRepository,
      GetTransactions getTransactions) {
    this.properties = properties;
    this.accountRepository = accountRepository;
    this.bankRepository = bankRepository;
    this.transactionRepository = transactionRepository;
    this.getTransactions = getTransactions;
  }

  @GetMapping({"", "/"})
  public String index(Model model) {
    var today = LocalDate.now();
    var account =
        accountRepository
            .findByOrganizationIdAndId(properties.getOrganizationId(), properties.getAccountId())
            .orElseThrow(() -> InfrastructureException.badRequest("something wrong"));
    var bank =
        bankRepository
            .findByOrganizationIdAndId(properties.getOrganizationId(), account.getBankId())
            .orElseThrow(() -> InfrastructureException.badRequest("something wrong"));

    var transactions =
        getTransactions.execute(
            new GetTransactionsCommand(
                properties.getOrganizationId(),
                properties.getAccountId(),
                today.with(TemporalAdjusters.firstDayOfMonth()),
                today.with(TemporalAdjusters.lastDayOfMonth())));

    model.addAttribute("account", new AccountModel(df, account, bank));
    model.addAttribute(
        "transactions", new TransactionsModel(df, dtf, dtfHourOfDay, bank, transactions));
    return "protected/index";
  }
}
