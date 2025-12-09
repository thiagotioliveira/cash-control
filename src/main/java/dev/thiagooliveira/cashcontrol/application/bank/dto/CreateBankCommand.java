package dev.thiagooliveira.cashcontrol.application.bank.dto;

import dev.thiagooliveira.cashcontrol.shared.Currency;

public record CreateBankCommand(String name, Currency currency) {}
