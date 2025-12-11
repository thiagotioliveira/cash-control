package dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.dtf;
import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.zoneId;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import java.time.LocalDate;
import java.util.*;

public class TransactionListModel {

  private final Map<LocalDate, List<TransactionItem>> content =
      new TreeMap<>(Comparator.reverseOrder());

  public TransactionListModel(List<GetTransactionItem> transactions) {
    transactions
        //            .stream()
        //        .sorted(
        //            (t1, t2) ->
        //                t1.occurredAt()
        //                    .map(date -> date)
        //                    .orElse(t1.dueDate().atStartOfDay(zoneId).toInstant())
        //                    .compareTo(
        //                        t2.occurredAt()
        //                            .map(date -> date)
        //                            .orElse(t2.dueDate().atStartOfDay(zoneId).toInstant())))
        .forEach(
        t -> {
          LocalDate key =
              t.occurredAt().map(date -> date.atZone(zoneId).toLocalDate()).orElse(t.dueDate());
          content.computeIfAbsent(key, k -> new ArrayList<>()).add(new TransactionItem(t));
        });
  }

  public String keyFormatted(LocalDate key) {
    var today = LocalDate.now(zoneId);
    if (today.equals(key)) {
      return "Hoje";
    } else if (today.minusDays(1).equals(key)) {
      return "Ontem";
    }
    return dtf.format(key);
  }

  public Map<LocalDate, List<TransactionItem>> getContent() {
    return content;
  }
}
