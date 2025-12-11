package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.account.ConfirmTransaction;
import dev.thiagooliveira.cashcontrol.application.account.UpdateScheduledTransaction;
import dev.thiagooliveira.cashcontrol.application.account.dto.ConfirmTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockDataProperties;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.AlertModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransactionItem;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransactionListModel;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.TransactionsModel;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/protected/transactions")
public class TransactionController {

  private final MockDataProperties properties;
  private final GetTransactions getTransactions;
  private final UpdateScheduledTransaction updateScheduledTransaction;
  private final ConfirmTransaction confirmTransaction;

  public TransactionController(
      MockDataProperties properties,
      GetTransactions getTransactions,
      UpdateScheduledTransaction updateScheduledTransaction,
      ConfirmTransaction confirmTransaction) {
    this.properties = properties;
    this.getTransactions = getTransactions;
    this.updateScheduledTransaction = updateScheduledTransaction;
    this.confirmTransaction = confirmTransaction;
  }

  @GetMapping
  public String getTransactions(Model model) {
    var today = LocalDate.now();
    var transactions =
        getTransactions.execute(
            new GetTransactionsCommand(
                properties.getOrganizationId(),
                properties.getAccountId(),
                today.with(TemporalAdjusters.firstDayOfMonth()),
                today.with(TemporalAdjusters.lastDayOfMonth())));
    model.addAttribute("transactions", new TransactionListModel(transactions));
    return "protected/transactions/transaction-list";
  }

  @PostMapping("/{transactionId}/review")
  public String postReviewTransaction(
      @PathVariable UUID transactionId,
      @ModelAttribute TransactionsModel.UpdateTransactionModel transactionModel,
      Model model) {
    if (!transactionId.equals(transactionModel.getId())) {
      throw InfrastructureException.badRequest("Invalid transaction id");
    }
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", transactionModel);
    return "protected/transactions/transaction-review";
  }

  @GetMapping("/{transactionId}/review")
  public String getReviewTransaction(@PathVariable UUID transactionId, Model model) {
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionsModel.UpdateTransactionModel(transaction));
    return "protected/transactions/transaction-review";
  }

  @GetMapping("/{transactionId}")
  public String getTransaction(@PathVariable UUID transactionId, Model model) {
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionItem(transaction));
    return "protected/transactions/transaction-details";
  }

  @PostMapping("/{transactionId}")
  public String postTransaction(
      @PathVariable UUID transactionId,
      @ModelAttribute TransactionsModel.UpdateTransactionModel transactionModel,
      Model model) {
    if (!transactionId.equals(transactionModel.getId())) {
      throw InfrastructureException.badRequest("Invalid transaction id");
    }
    if (transactionModel.getOccurredAt() != null) {
      confirmTransaction.execute(
          new ConfirmTransactionCommand(
              properties.getOrganizationId(),
              properties.getUserId(),
              properties.getAccountId(),
              transactionId,
              transactionModel.getOccurredAt().atZone(zoneId).toInstant(),
              transactionModel.getAmount()));
    } else {
      updateScheduledTransaction.execute(
          new UpdateScheduledTransactionCommand(
              properties.getOrganizationId(),
              properties.getUserId(),
              properties.getAccountId(),
              transactionId,
              transactionModel.getDescription(),
              transactionModel.getAmount(),
              transactionModel.getDueDayOfMonth(),
              Optional.empty()));
    }
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionItem(transaction));
    model.addAttribute("alert", AlertModel.success("Transação atualizada com sucesso!"));
    return "protected/transactions/transaction-details";
  }

  public GetTransactionItem getTransactionItem(UUID transactionId) {
    return this.getTransactions
        .execute(
            new GetTransactionCommand(
                properties.getOrganizationId(), properties.getAccountId(), transactionId))
        .orElseThrow(() -> InfrastructureException.notFound("Transaction not found"));
  }
}
