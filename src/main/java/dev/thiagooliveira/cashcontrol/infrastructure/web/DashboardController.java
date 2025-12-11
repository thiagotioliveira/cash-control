package dev.thiagooliveira.cashcontrol.infrastructure.web;

import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsPageCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockDataProperties;
import dev.thiagooliveira.cashcontrol.infrastructure.web.mapper.TransactionChartMapper;
import dev.thiagooliveira.cashcontrol.shared.Pageable;
import java.time.LocalDate;
import java.time.Month;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

  private final MockDataProperties properties;
  private final GetTransactions getTransactions;

  public DashboardController(MockDataProperties properties, GetTransactions getTransactions) {
    this.properties = properties;
    this.getTransactions = getTransactions;
  }

  @GetMapping
  public String showChart(Model model) {
    var transactions =
        getTransactions.execute(
            new GetTransactionsPageCommand(
                properties.getOrganizationId(),
                properties.getAccountId(),
                LocalDate.of(2025, Month.NOVEMBER, 25),
                LocalDate.of(2026, Month.DECEMBER, 31),
                new Pageable(0, Integer.MAX_VALUE)));
    model.addAttribute("mixedData", TransactionChartMapper.toBalanceData(transactions.content()));
    model.addAttribute(
        "monthlyPieCharts", TransactionChartMapper.toMonthlyCategoryData(transactions.content()));
    return "dashboard";
  }
}
