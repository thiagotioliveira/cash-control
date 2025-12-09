package dev.thiagooliveira.cashcontrol.infrastructure.web.mapper;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionChartMapper {

  // ===========================
  // Dados para mixed chart
  // ===========================
  public static List<BalanceChartItem> toBalanceData(List<GetTransactionItem> items) {

    Map<YearMonth, List<GetTransactionItem>> grouped =
        items.stream().collect(Collectors.groupingBy(t -> YearMonth.from(t.dueDate())));

    List<BalanceChartItem> result = new ArrayList<>();

    grouped.forEach(
        (yearMonth, list) -> {
          BigDecimal credit =
              list.stream()
                  .filter(t -> t.type().isCredit())
                  .map(GetTransactionItem::amount)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);

          BigDecimal debit =
              list.stream()
                  .filter(t -> t.type().isDebit())
                  .map(GetTransactionItem::amount)
                  .reduce(BigDecimal.ZERO, BigDecimal::add);

          result.add(new BalanceChartItem(yearMonth, credit, debit));
        });

    result.sort(Comparator.comparing(BalanceChartItem::getYearMonth));

    BigDecimal acumulado = BigDecimal.ZERO;

    for (BalanceChartItem item : result) {
      acumulado = acumulado.add(item.getCredit().subtract(item.getDebit()));
      item.setBalance(acumulado);
    }

    return result;
  }

  public static class BalanceChartItem {
    private YearMonth yearMonth;
    private BigDecimal credit;
    private BigDecimal debit;
    private BigDecimal balance;

    public BalanceChartItem() {}

    public BalanceChartItem(YearMonth yearMonth, BigDecimal credit, BigDecimal debit) {
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

  // ===========================
  // Dados para gráficos de pizza por categoria
  // ===========================
  public static List<MonthlyCategoryItem> toMonthlyCategoryData(List<GetTransactionItem> items) {

    Map<YearMonth, List<GetTransactionItem>> grouped =
        items.stream().collect(Collectors.groupingBy(t -> YearMonth.from(t.dueDate())));

    List<MonthlyCategoryItem> result = new ArrayList<>();

    grouped.forEach(
        (yearMonth, list) -> {

          // Agrupar por categoria
          List<CategoryChartItem> categoryData =
              list.stream()
                  .collect(
                      Collectors.groupingBy(
                          GetTransactionItem::categoryName,
                          LinkedHashMap::new,
                          Collectors.reducing(
                              BigDecimal.ZERO, GetTransactionItem::amount, BigDecimal::add)))
                  .entrySet()
                  .stream()
                  .map(
                      e -> {
                        // Pegar a cor da primeira transação da categoria
                        String color =
                            list.stream()
                                .filter(t -> t.categoryName().equals(e.getKey()))
                                .findFirst()
                                .map(GetTransactionItem::categoryHashColor)
                                .orElse("#007bff");

                        return new CategoryChartItem(e.getKey(), e.getValue(), color);
                      })
                  .toList();

          result.add(new MonthlyCategoryItem(yearMonth, categoryData));
        });

    result.sort(Comparator.comparing(MonthlyCategoryItem::getYearMonth));
    return result;
  }

  public static class MonthlyCategoryItem {
    private YearMonth yearMonth;
    private List<CategoryChartItem> transactions;

    public MonthlyCategoryItem(YearMonth yearMonth, List<CategoryChartItem> transactions) {
      this.yearMonth = yearMonth;
      this.transactions = transactions;
    }

    public YearMonth getYearMonth() {
      return yearMonth;
    }

    public List<CategoryChartItem> getTransactions() {
      return transactions;
    }
  }

  public static class CategoryChartItem {
    private String categoryName;
    private BigDecimal amount;
    private String categoryHashColor;

    public CategoryChartItem(String categoryName, BigDecimal amount, String categoryHashColor) {
      this.categoryName = categoryName;
      this.amount = amount;
      this.categoryHashColor = categoryHashColor;
    }

    public String getCategoryName() {
      return categoryName;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public String getCategoryHashColor() {
      return categoryHashColor;
    }
  }
}
