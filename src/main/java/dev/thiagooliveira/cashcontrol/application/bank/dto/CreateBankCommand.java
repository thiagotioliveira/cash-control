package dev.thiagooliveira.cashcontrol.application.bank.dto;

import dev.thiagooliveira.cashcontrol.shared.Currency;
import java.util.UUID;

public record CreateBankCommand(UUID organizationId, UUID userId, String name, Currency currency) {}
