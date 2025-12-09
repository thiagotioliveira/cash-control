package dev.thiagooliveira.cashcontrol.infrastructure.web;

import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockDataProperties;
import dev.thiagooliveira.cashcontrol.infrastructure.web.mapper.TransactionChartMapper;
import java.time.LocalDate;
import java.time.Month;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
            new GetTransactionsCommand(
                properties.getOrganizationId(),
                properties.getAccountId(),
                LocalDate.of(2025, Month.NOVEMBER, 25),
                LocalDate.of(2026, Month.DECEMBER, 31)));
    model.addAttribute("mixedData", TransactionChartMapper.toBalanceData(transactions));
    model.addAttribute(
        "monthlyPieCharts", TransactionChartMapper.toMonthlyCategoryData(transactions));
    return "dashboard";
  }
}
