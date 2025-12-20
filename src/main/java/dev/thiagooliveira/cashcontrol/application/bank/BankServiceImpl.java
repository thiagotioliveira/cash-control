package dev.thiagooliveira.cashcontrol.application.bank;

import dev.thiagooliveira.cashcontrol.application.bank.dto.CreateBankCommand;
import dev.thiagooliveira.cashcontrol.application.outbound.BankRepository;
import dev.thiagooliveira.cashcontrol.domain.bank.BankSummary;
import java.util.Optional;
import java.util.UUID;

public class BankServiceImpl implements BankService {

  private final CreateBank createBank;
  private final BankRepository bankRepository;

  public BankServiceImpl(CreateBank createBank, BankRepository bankRepository) {
    this.createBank = createBank;
    this.bankRepository = bankRepository;
  }

  @Override
  public BankSummary createBank(CreateBankCommand command) {
    return this.createBank.execute(command);
  }

  @Override
  public Optional<BankSummary> get(UUID organizationId, UUID bankId) {
    return bankRepository.findByOrganizationIdAndId(organizationId, bankId);
  }

  @Override
  public Optional<BankSummary> get(UUID organizationId, String name) {
    return this.bankRepository.findByOrganizationIdAndName(organizationId, name);
  }
}
