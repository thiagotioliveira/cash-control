package dev.thiagooliveira.cashcontrol.infrastructure.transactional.bank;

import dev.thiagooliveira.cashcontrol.application.bank.BankService;
import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import java.util.Optional;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BankServiceProxy implements BankService {

  private final BankService bankService;

  public BankServiceProxy(BankService bankService) {
    this.bankService = bankService;
  }

  @Override
  public BankSummary createBank(CreateBankCommand command) {
    return this.bankService.createBank(command);
  }

  @Override
  public Optional<BankSummary> get(UUID organizationId, UUID bankId) {
    return this.bankService.get(organizationId, bankId);
  }
}
