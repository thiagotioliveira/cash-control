package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {
  void confirmTransaction(ConfirmTransactionCommand command);

  void createDeposit(CreateTransactionCommand command);

  void createWithdrawal(CreateTransactionCommand command);

  void createPayable(CreateScheduledTransactionCommand command);

  void createReceivable(CreateScheduledTransactionCommand command);

  void revertTransaction(RevertTransactionCommand command);

  void updateScheduledTransaction(UpdateScheduledTransactionCommand command);

  AccountSummary createAccount(CreateAccountCommand command);

  Optional<AccountSummary> get(UUID organizationId, UUID accountId);

  List<AccountSummary> get(UUID organizationId);
}
