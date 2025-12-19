package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import static dev.thiagooliveira.cashcontrol.infrastructure.web.manager.FormattersUtils.*;

import dev.thiagooliveira.cashcontrol.application.account.*;
import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.application.category.CategoryService;
import dev.thiagooliveira.cashcontrol.application.exception.ApplicationException;
import dev.thiagooliveira.cashcontrol.application.transaction.TransactionService;
import dev.thiagooliveira.cashcontrol.application.transaction.dto.*;
import dev.thiagooliveira.cashcontrol.domain.exception.DomainException;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import dev.thiagooliveira.cashcontrol.domain.user.security.Context;
import dev.thiagooliveira.cashcontrol.infrastructure.exception.InfrastructureException;
import dev.thiagooliveira.cashcontrol.infrastructure.web.manager.model.*;
import dev.thiagooliveira.cashcontrol.shared.Recurrence;
import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/protected/transactions")
public class TransactionController {

  private final Context context;
  private final TransactionService transactionService;
  private final CategoryService categoryService;

  public TransactionController(
      Context context,
      AccountService accountService,
      TransactionService transactionService,
      CategoryService categoryService) {
    this.context = context;
    this.transactionService = transactionService;
    this.categoryService = categoryService;
  }

  @GetMapping("/{yearMonth:\\d{4}-\\d{2}}")
  public String getTransactions(
      @RequestParam(required = false) TransactionType type,
      @RequestParam(required = false) TransactionStatus status,
      @PathVariable YearMonth yearMonth,
      Model model) {
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();
    var transactions =
        transactionService.get(
            new GetTransactionsCommand(
                context.getUser().organizationId(), context.getAccountId(), startDate, endDate));
    return buildGetTransactionModel(transactions, type, status, model);
  }

  @GetMapping
  public String getTransactions(
      @RequestParam(required = false) TransactionType type,
      @RequestParam(required = false) TransactionStatus status,
      Model model) {
    var today = LocalDate.now(zoneId);
    var transactions =
        transactionService.get(
            new GetTransactionsCommand(
                context.getUser().organizationId(),
                context.getAccountId(),
                today.with(TemporalAdjusters.firstDayOfMonth()),
                today.with(TemporalAdjusters.lastDayOfMonth())));
    return buildGetTransactionModel(transactions, type, status, model);
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

  @GetMapping("/review")
  public String getReviewTransaction(
      @ModelAttribute("transaction") TransactionActionSheetModel.TransactionForm form,
      Model model) {
    return postReviewTransaction(form, model);
  }

  @PostMapping("/review")
  public String postReviewTransaction(
      @ModelAttribute TransactionActionSheetModel.TransactionForm form, Model model) {
    var categories =
        categoryService
            .get(context.getUser().organizationId(), form.getCategoryId())
            .orElseThrow(() -> InfrastructureException.notFound("Category not found"));
    form.setCategoryName(categories.name());
    if (form.getDescription() == null) {
      form.setDescription(form.getCategoryName());
    }
    if (form.getStartDueDate() == null) {
      form.setStartDueDate(
          form.getOccurredAt() != null ? form.getOccurredAt().toLocalDate() : null);
    }
    model.addAttribute("transaction", form);
    return "protected/transactions/transaction-review";
  }

  @PostMapping
  public String postTransaction(
      @ModelAttribute TransactionActionSheetModel.TransactionForm form,
      Model model,
      RedirectAttributes redirectAttributes) {
    var categories =
        categoryService
            .get(context.getUser().organizationId(), form.getCategoryId())
            .orElseThrow(() -> InfrastructureException.notFound("Category not found"));
    try {
      if (categories.type().isCredit()) {
        if (form.getOccurredAt() != null) {
          transactionService.createDeposit(
              new CreateDepositCommand(
                  context.getUser().organizationId(),
                  context.getUser().id(),
                  context.getAccountId(),
                  form.getOccurredAt().atZone(zoneId).toInstant(),
                  form.getCategoryId(),
                  form.getAmount(),
                  form.getDescription()));
        } else {
          transactionService.createReceivable(
              new CreateReceivableCommand(
                  context.getUser().organizationId(),
                  context.getUser().id(),
                  context.getAccountId(),
                  form.getCategoryId(),
                  form.getAmount(),
                  form.getStartDueDate(),
                  Recurrence.valueOf(form.getRecurrence()),
                  Optional.ofNullable(form.getInstallments())));
        }
      } else {
        if (form.getOccurredAt() != null) {
          transactionService.createWithdrawal(
              new CreateWithdrawalCommand(
                  context.getUser().organizationId(),
                  context.getUser().id(),
                  context.getAccountId(),
                  form.getOccurredAt().atZone(zoneId).toInstant(),
                  form.getCategoryId(),
                  form.getAmount(),
                  form.getDescription()));
        } else {
          transactionService.createPayable(
              new CreatePayableCommand(
                  context.getUser().organizationId(),
                  context.getUser().id(),
                  context.getAccountId(),
                  form.getCategoryId(),
                  form.getAmount(),
                  form.getStartDueDate(),
                  Recurrence.valueOf(form.getRecurrence()),
                  Optional.ofNullable(form.getInstallments())));
        }
      }
      return "redirect:/protected/transactions";
    } catch (DomainException | ApplicationException e) {
      redirectAttributes.addFlashAttribute("transaction", form);
      redirectAttributes.addFlashAttribute("alert", AlertModel.error(e.getMessage()));
      return "redirect:/protected/transactions/review";
    }
  }

  @GetMapping("/{transactionId}/review")
  public String getReviewTransaction(@PathVariable UUID transactionId, Model model) {
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionActionSheetModel.TransactionForm(transaction));
    return "protected/transactions/transaction-review";
  }

  @GetMapping("/{transactionId:[0-9a-fA-F\\-]{36}}")
  public String getTransaction(@PathVariable UUID transactionId, Model model) {
    var transaction = getTransactionItem(transactionId);
    model.addAttribute("transaction", new TransactionDetailsModel(transaction, "/protected/"));
    return "protected/transactions/transaction-details";
  }

  @PostMapping("/{transactionId}/delete")
  public String deleteTransaction(
      @PathVariable UUID transactionId, RedirectAttributes redirectAttributes) {
    try {
      transactionService.revertTransaction(
          new RevertTransactionCommand(
              context.getUser().organizationId(),
              context.getAccountId(),
              transactionId,
              context.getUser().id()));
      redirectAttributes.addFlashAttribute(
          "alert", AlertModel.success("Transação revertida com sucesso!"));
      return "redirect:/protected/transactions";
    } catch (DomainException | ApplicationException e) {
      redirectAttributes.addFlashAttribute("alert", AlertModel.error(e.getMessage()));
      return "redirect:/protected/transactions/" + transactionId;
    }
  }

  @PostMapping("/{transactionId}")
  public String postTransaction(
      @PathVariable UUID transactionId,
      @ModelAttribute TransactionActionSheetModel.TransactionForm form,
      Model model,
      RedirectAttributes redirectAttributes) {
    if (!transactionId.equals(form.getId())) {
      throw InfrastructureException.badRequest("Invalid transaction id");
    }
    try {
      if (form.getOccurredAt() != null) {
        transactionService.confirm(
            new ConfirmScheduledTransactionCommand(
                context.getUser().organizationId(),
                context.getUser().id(),
                context.getAccountId(),
                transactionId,
                form.getOccurredAt().atZone(zoneId).toInstant(),
                form.getAmount()));
      } else {
        transactionService.update(
            new UpdateScheduledTransactionCommand(
                context.getUser().organizationId(),
                context.getUser().id(),
                context.getAccountId(),
                transactionId,
                form.getDescription(),
                form.getAmount(),
                form.getStartDueDate(),
                Optional.empty()));
      }
      var transaction = getTransactionItem(transactionId);
      model.addAttribute("transaction", new TransactionDetailsModel(transaction, "/protected/"));
      model.addAttribute("alert", AlertModel.success("Transação atualizada com sucesso!"));
      return "protected/transactions/transaction-details";
    } catch (DomainException | ApplicationException e) {
      redirectAttributes.addFlashAttribute("alert", AlertModel.error(e.getMessage()));
      return "redirect:/protected/transactions/" + transactionId;
    }
  }

  private static String buildGetTransactionModel(
      List<TransactionSummary> transactions,
      TransactionType type,
      TransactionStatus status,
      Model model) {
    model.addAttribute(
        "transactions",
        new TransactionListModel(
            transactions.stream()
                .filter(
                    t -> {
                      if (type != null) {
                        return t.type().equals(type);
                      }
                      return true;
                    })
                .filter(
                    t -> {
                      if (status != null) {
                        return t.status().equals(status);
                      }
                      return true;
                    })
                .toList()));
    return "protected/transactions/transaction-list";
  }

  public TransactionSummary getTransactionItem(UUID transactionId) {
    return transactionService
        .get(
            new GetTransactionCommand(
                context.getUser().organizationId(), context.getAccountId(), transactionId))
        .orElseThrow(() -> InfrastructureException.notFound("Transaction not found"));
  }
}
