package dev.thiagooliveira.cashcontrol.application.account;

import dev.thiagooliveira.cashcontrol.application.account.dto.*;
import dev.thiagooliveira.cashcontrol.domain.account.AccountSummary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountService {

  void applyCredit(ApplyCreditCommand command);

  void revertCredit(RevertCreditCommand command);

  void applyDebit(ApplyDebitCommand command);

  void revertDebit(RevertDebitCommand command);

  AccountSummary createAccount(CreateAccountCommand command);

  Optional<AccountSummary> get(UUID organizationId, UUID accountId);

  List<AccountSummary> get(UUID organizationId);
}
