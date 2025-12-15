package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper;

import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MonthlyIncomeExpensesMapper {

  public List<MonthlyIncomeExpenses> toMonthlyIncomeExpensesData(List<TransactionSummary> items) {
    Map<YearMonth, List<TransactionSummary>> grouped =
        items.stream().collect(Collectors.groupingBy(t -> YearMonth.from(t.dueDate())));

    List<MonthlyIncomeExpenses> result = new ArrayList<>();

    grouped.forEach(
        (yearMonth, list) -> {
          BigDecimal credit =
              list.stream()
                  .filter(t -> t.type().isCredit())
                  .map(TransactionSummary::amount)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);

          BigDecimal debit =
              list.stream()
                  .filter(t -> t.type().isDebit())
                  .map(TransactionSummary::amount)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);

          result.add(new MonthlyIncomeExpenses(yearMonth, credit, debit));
        });

    result.sort(Comparator.comparing(MonthlyIncomeExpenses::getYearMonth));

    BigDecimal acumulado = BigDecimal.ZERO;

    for (MonthlyIncomeExpenses item : result) {
      acumulado = acumulado.add(item.getCredit().subtract(item.getDebit()));
      item.setBalance(acumulado);
    }

    return result;
  }

  public static class MonthlyIncomeExpenses {
    private YearMonth yearMonth;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;

    public MonthlyIncomeExpenses() {}

    public MonthlyIncomeExpenses(YearMonth yearMonth, BigDecimal credit, BigDecimal debit) {
      this.yearMonth = yearMonth;
      this.credit = credit;
      this.debit = debit;
    }

    public YearMonth getYearMonth() {
      return yearMonth;
    }

    public BigDecimal getCredit() {
      return credit;
    }

    public BigDecimal getDebit() {
      return debit;
    }

    public BigDecimal getBalance() {
      return balance;
    }

    public void setYearMonth(YearMonth yearMonth) {
      this.yearMonth = yearMonth;
    }

    public void setCredit(BigDecimal credit) {
      this.credit = credit;
    }

    public void setDebit(BigDecimal debit) {
      this.debit = debit;
    }

    public void setBalance(BigDecimal balance) {
      this.balance = balance;
    }
  }
}
