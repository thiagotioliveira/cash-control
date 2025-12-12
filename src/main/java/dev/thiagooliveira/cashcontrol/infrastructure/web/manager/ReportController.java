package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockContext;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper.MonthlyCategoryMapper;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper.MonthlyIncomeExpensesMapper;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/protected/reports")
public class ReportController {

  private final MockContext context;
  private final GetTransactions getTransactions;
  private final MonthlyCategoryMapper monthlyCategoryMapper;
  private final MonthlyIncomeExpensesMapper monthlyIncomeExpensesMapper;

  public ReportController(
      MockContext context,
      GetTransactions getTransactions,
      MonthlyCategoryMapper monthlyCategoryMapper,
      MonthlyIncomeExpensesMapper monthlyIncomeExpensesMapper) {
    this.context = context;
    this.getTransactions = getTransactions;
    this.monthlyCategoryMapper = monthlyCategoryMapper;
    this.monthlyIncomeExpensesMapper = monthlyIncomeExpensesMapper;
  }

  @GetMapping
  public String index(Model model) {
    return buildReportModel(model, LocalDate.now(zoneId));
  }

  @GetMapping("/{yearMonth}")
  public String index(@PathVariable YearMonth yearMonth, Model model) {
    return buildReportModel(model, yearMonth.atDay(1));
  }

  private String buildReportModel(Model model, LocalDate localDate) {
    var transactions =
        getTransactions
            .execute(
                new GetTransactionsCommand(
                    context.getOrganizationId(),
                    context.getAccountId(),
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
