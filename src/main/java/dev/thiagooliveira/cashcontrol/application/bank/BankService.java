package dev.thiagooliveira.cashcontrol.application.bank;

import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import java.util.Optional;
import java.util.UUID;

public interface BankService {

  BankSummary createBank(CreateBankCommand command);

  Optional<BankSummary> get(UUID organizationId, UUID bankId);
}
