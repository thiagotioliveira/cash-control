package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.mapper;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MonthlyCategoryMapper {

  public List<MonthlyCategoryItem> toMonthlyCategoryData(List<GetTransactionItem> items) {

    Map<YearMonth, List<GetTransactionItem>> grouped =
        items.stream().collect(Collectors.groupingBy(t -> YearMonth.from(t.dueDate())));

    List<MonthlyCategoryItem> result = new ArrayList<>();

    grouped.forEach(
        (yearMonth, list) -> {

          // Agrupar por categoria
          List<CategoryItem> categoryData =
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

                        return new CategoryItem(e.getKey(), e.getValue(), color);
                      })
                  .toList();

          result.add(new MonthlyCategoryItem(yearMonth, categoryData));
        });

    result.sort(Comparator.comparing(MonthlyCategoryItem::getYearMonth));
    return result;
  }

  public static class MonthlyCategoryItem {
    private final YearMonth yearMonth;
    private final List<CategoryItem> transactions;

    public MonthlyCategoryItem(YearMonth yearMonth, List<CategoryItem> transactions) {
      this.yearMonth = yearMonth;
      this.transactions = transactions;
    }

    public YearMonth getYearMonth() {
      return yearMonth;
    }

    public List<CategoryItem> getTransactions() {
      return transactions;
    }
  }

  public static class CategoryItem {
    private final String categoryName;
    private final BigDecimal amount;
    private final String categoryHashColor;

    public CategoryItem(String categoryName, BigDecimal amount, String categoryHashColor) {
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
