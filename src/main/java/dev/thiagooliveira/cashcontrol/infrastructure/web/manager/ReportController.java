package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.account.AccountService;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper.MonthlyCategoryMapper;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper.MonthlyIncomeExpensesMapper;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected")
public class ReportController {

  private final SecurityContext securityContext;
  private final AccountService accountService;
  private final TransactionService transactionService;
  private final MonthlyCategoryMapper monthlyCategoryMapper;
  private final MonthlyIncomeExpensesMapper monthlyIncomeExpensesMapper;

  public ReportController(
      SecurityContext securityContext,
      AccountService accountService,
      TransactionService transactionService,
      MonthlyCategoryMapper monthlyCategoryMapper,
      MonthlyIncomeExpensesMapper monthlyIncomeExpensesMapper) {
    this.securityContext = securityContext;
    this.accountService = accountService;
    this.transactionService = transactionService;
    this.monthlyCategoryMapper = monthlyCategoryMapper;
    this.monthlyIncomeExpensesMapper = monthlyIncomeExpensesMapper;
  }

  @GetMapping("/accounts/{accountId}/reports")
  public String indexWithAccount(@PathVariable UUID accountId, Model model) {
    return buildReportModel(Optional.of(accountId), model, LocalDate.now(zoneId));
  }

  @GetMapping("/accounts/{accountId}/reports/{yearMonth}")
  public String indexWithAccount(
      @PathVariable UUID accountId, @PathVariable YearMonth yearMonth, Model model) {
    return buildReportModel(Optional.of(accountId), model, yearMonth.atDay(1));
  }

  @GetMapping("/reports")
  public String index(Model model) {
    return buildReportModel(Optional.empty(), model, LocalDate.now(zoneId));
  }

  @GetMapping("/reports/{yearMonth}")
  public String index(@PathVariable YearMonth yearMonth, Model model) {
    return buildReportModel(Optional.empty(), model, yearMonth.atDay(1));
  }

  private String buildReportModel(Optional<UUID> accountId, Model model, LocalDate localDate) {
    var transactions =
        transactionService
            .get(
                new GetTransactionsCommand(
                    securityContext.getUser().organizationId(),
                    accountId,
                    localDate.with(TemporalAdjusters.firstDayOfMonth()),
                    localDate.with(TemporalAdjusters.lastDayOfMonth())))
            .stream()
            .filter(t -> t.status().isConfirmed())
            .filter(t -> !t.categoryType().isTransfer())
            .toList();

    var accountName = "Todas as contas";
    if (accountId.isPresent()) {
      accountName =
          this.accountService
              .get(securityContext.getUser().organizationId(), accountId.get())
              .orElseThrow(() -> InfrastructureException.notFound("account not found"))
              .name();
    }
    String title = String.format("%s (%s)", accountName, YearMonth.from(localDate));
    model.addAttribute("title", title);
    model.addAttribute(
        "monthlyIncomeExpenses",
        monthlyIncomeExpensesMapper.toMonthlyIncomeExpensesData(transactions));
    model.addAttribute(
        "monthlyCategory", monthlyCategoryMapper.toMonthlyCategoryData(transactions));
    return "protected/reports/report";
  }
}
