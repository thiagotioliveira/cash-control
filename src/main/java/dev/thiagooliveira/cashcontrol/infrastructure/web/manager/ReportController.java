package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.shared.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.domain.user.security.SecurityContext;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper.MonthlyCategoryMapper;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper.MonthlyIncomeExpensesMapper;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/accounts")
public class ReportController {

  private final SecurityContext securityContext;
  private final TransactionService transactionService;
  private final MonthlyCategoryMapper monthlyCategoryMapper;
  private final MonthlyIncomeExpensesMapper monthlyIncomeExpensesMapper;

  public ReportController(
      SecurityContext securityContext,
      TransactionService transactionService,
      MonthlyCategoryMapper monthlyCategoryMapper,
      MonthlyIncomeExpensesMapper monthlyIncomeExpensesMapper) {
    this.securityContext = securityContext;
    this.transactionService = transactionService;
    this.monthlyCategoryMapper = monthlyCategoryMapper;
    this.monthlyIncomeExpensesMapper = monthlyIncomeExpensesMapper;
  }

  @GetMapping("/{accountId}/reports")
  public String index(@PathVariable UUID accountId, Model model) {
    return buildReportModel(accountId, model, LocalDate.now(zoneId));
  }

  @GetMapping("/{accountId}/reports/{yearMonth}")
  public String index(
      @PathVariable UUID accountId, @PathVariable YearMonth yearMonth, Model model) {
    return buildReportModel(accountId, model, yearMonth.atDay(1));
  }

  private String buildReportModel(UUID accountId, Model model, LocalDate localDate) {
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
            .toList();
    model.addAttribute("title", YearMonth.from(localDate));
    model.addAttribute(
        "monthlyIncomeExpenses",
        monthlyIncomeExpensesMapper.toMonthlyIncomeExpensesData(transactions));
    model.addAttribute(
        "monthlyCategory", monthlyCategoryMapper.toMonthlyCategoryData(transactions));
    return "protected/reports/report";
  }
}
