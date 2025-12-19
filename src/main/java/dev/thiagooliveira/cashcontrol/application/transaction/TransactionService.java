package dev.thiagooliveira.cashcontrol.application.transaction;

import dev.thiagooliveira.cashcontrol.application.transaction.dto.*;
import dev.thiagooliveira.cashcontrol.domain.transaction.TransactionSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionService {

  boolean isLatestTransaction(UUID organizationId, UUID accountId, UUID id);

  List<TransactionSummary> get(GetTransactionsCommand command);

  Optional<TransactionSummary> get(GetTransactionCommand command);

  void createDeposit(CreateDepositCommand command);

  void confirm(ConfirmTransactionCommand command);

  void confirm(ConfirmScheduledTransactionCommand command);

  void confirm(ConfirmRevertTransactionCommand command);

  void update(UpdateScheduledTransactionCommand command);

  void createWithdrawal(CreateWithdrawalCommand command);

  void createPayable(CreatePayableCommand command);

  void createReceivable(CreateReceivableCommand command);

  void revertTransaction(RevertTransactionCommand command);
}
