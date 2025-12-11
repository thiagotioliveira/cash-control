package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.account.*;
import dev.thiagooliveira.cashcontrol.application.account.dto.ConfirmTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.CreateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.CreateTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.account.dto.UpdateScheduledTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.outbound.CategoryRepository;
import dev.thiagooliveira.cashcontrol.application.transaction.GetTransactions;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionCommand;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionItem;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.GetTransactionsCommand;
import dev.thiagooliveira.cashcontrol.infrastructure.config.mockdata.MockDataProperties;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.*;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
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
  private final CategoryRepository categoryRepository;
  private final UpdateScheduledTransaction updateScheduledTransaction;
  private final ConfirmTransaction confirmTransaction;
  private final CreateDeposit createDeposit;
  private final CreateWithdrawal createWithdrawal;
  private final CreateReceivable createReceivable;
  private final CreatePayable createPayable;

  public TransactionController(
      MockDataProperties properties,
      GetTransactions getTransactions,
      CategoryRepository categoryRepository,
      UpdateScheduledTransaction updateScheduledTransaction,
      ConfirmTransaction confirmTransaction,
      CreateDeposit createDeposit,
      CreateWithdrawal createWithdrawal,
      CreateReceivable createReceivable,
      CreatePayable createPayable) {
    this.properties = properties;
    this.getTransactions = getTransactions;
    this.categoryRepository = categoryRepository;
    this.updateScheduledTransaction = updateScheduledTransaction;
    this.confirmTransaction = confirmTransaction;
    this.createDeposit = createDeposit;
    this.createWithdrawal = createWithdrawal;
    this.createReceivable = createReceivable;
    this.createPayable = createPayable;
  }

  @GetMapping
  public String getTransactions(
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status,
      Model model) {
    var today = LocalDate.now();
    var transactions =
        getTransactions.execute(
            new GetTransactionsCommand(
                properties.getOrganizationId(),
                properties.getAccountId(),
                today.with(TemporalAdjusters.firstDayOfMonth()),
                today.with(TemporalAdjusters.lastDayOfMonth())));
    model.addAttribute(
        "transactions",
        new TransactionListModel(
            transactions.stream()
                .filter(
                    t -> {
                      if (type != null) {
                        return t.type().equals(TransactionType.valueOf(type));
                      }
                      return true;
                    })
                .filter(
                    t -> {
                      if (status != null) {
                        return t.status().equals(TransactionStatus.valueOf(status));
                      }
                      return true;
                    })
                .toList()));
    return "protected/transactions/transaction-list";
  }

  @PostMapping("/{transactionId}/review")
  public String postReviewTransaction(
      @PathVariable UUID transactionId,
      @ModelAttribute TransactionActionSheetModel.TransactionForm form,
      Model model) {
    if (!transactionId.equals(form.getId())) {
      throw InfrastructureException.badRequest("Invalid transaction id");
    }
    //    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", form);
    return "protected/transactions/transaction-review";
  }

  @PostMapping("/review")
  public String postReviewTransaction(
      @ModelAttribute TransactionActionSheetModel.TransactionForm form, Model model) {
    var categories =
        categoryRepository
            .findByOrganizationIdAndId(properties.getOrganizationId(), form.getCategoryId())
            .orElseThrow(() -> InfrastructureException.notFound("Category not found"));
    form.setCategoryName(categories.getName());
    if (form.getDescription() == null) {
      form.setDescription(form.getCategoryName());
    }
    if (form.getDueDayOfMonth() == null) {
      form.setDueDayOfMonth(
          form.getOccurredAt() != null ? form.getOccurredAt().getDayOfMonth() : null);
    }
    model.addAttribute("transaction", form);
    return "protected/transactions/transaction-review";
  }

  @PostMapping
  public String postTransaction(
      @ModelAttribute TransactionActionSheetModel.TransactionForm form, Model model) {
    var categories =
        categoryRepository
            .findByOrganizationIdAndId(properties.getOrganizationId(), form.getCategoryId())
            .orElseThrow(() -> InfrastructureException.notFound("Category not found"));
    if (categories.getType().isCredit()) {
      if (form.getOccurredAt() != null) {
        createDeposit.execute(
            new CreateTransactionCommand(
                properties.getOrganizationId(),
                properties.getUserId(),
                properties.getAccountId(),
                form.getOccurredAt().atZone(zoneId).toInstant(),
                form.getCategoryId(),
                form.getAmount(),
                form.getDescription()));
      } else {
        createReceivable.execute(
            new CreateScheduledTransactionCommand(
                properties.getOrganizationId(),
                properties.getUserId(),
                properties.getAccountId(),
                form.getCategoryId(),
                form.getAmount(),
                form.getDueDayOfMonth(),
                Recurrence.valueOf(form.getRecurrence()),
                Optional.ofNullable(form.getDueDayOfMonth())));
      }
    } else {
      if (form.getOccurredAt() != null) {
        createWithdrawal.execute(
            new CreateTransactionCommand(
                properties.getOrganizationId(),
                properties.getUserId(),
                properties.getAccountId(),
                form.getOccurredAt().atZone(zoneId).toInstant(),
                form.getCategoryId(),
                form.getAmount(),
                form.getDescription()));
      } else {
        createPayable.execute(
            new CreateScheduledTransactionCommand(
                properties.getOrganizationId(),
                properties.getUserId(),
                properties.getAccountId(),
                form.getCategoryId(),
                form.getAmount(),
                form.getDueDayOfMonth(),
                Recurrence.valueOf(form.getRecurrence()),
                Optional.ofNullable(form.getDueDayOfMonth())));
      }
    }
    return "redirect:/protected/transactions";
  }

  @GetMapping("/{transactionId}/review")
  public String getReviewTransaction(@PathVariable UUID transactionId, Model model) {
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionActionSheetModel.TransactionForm(transaction));
    return "protected/transactions/transaction-review";
  }

  @GetMapping("/{transactionId}")
  public String getTransaction(@PathVariable UUID transactionId, Model model) {
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionDetailsModel(transaction, "/protected/"));
    return "protected/transactions/transaction-details";
  }

  @PostMapping("/{transactionId}")
  public String postTransaction(
      @PathVariable UUID transactionId,
      @ModelAttribute TransactionActionSheetModel.TransactionForm form,
      Model model) {
    if (!transactionId.equals(form.getId())) {
      throw InfrastructureException.badRequest("Invalid transaction id");
    }
    if (form.getOccurredAt() != null) {
      confirmTransaction.execute(
          new ConfirmTransactionCommand(
              properties.getOrganizationId(),
              properties.getUserId(),
              properties.getAccountId(),
              transactionId,
              form.getOccurredAt().atZone(zoneId).toInstant(),
              form.getAmount()));
    } else {
      updateScheduledTransaction.execute(
          new UpdateScheduledTransactionCommand(
              properties.getOrganizationId(),
              properties.getUserId(),
              properties.getAccountId(),
              transactionId,
              form.getDescription(),
              form.getAmount(),
              form.getDueDayOfMonth(),
              Optional.empty()));
    }
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionDetailsModel(transaction, "/protected/"));
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
